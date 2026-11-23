#!/usr/bin/env python3
"""Push -> wait for GitHub Actions -> download APK. Run inside the repo folder."""
import getpass, io, json, os, re, subprocess, sys, time, urllib.request, urllib.error, zipfile

def sh(*a):
    return subprocess.check_output(a, text=True).strip()

remote = sh("git", "remote", "get-url", "origin")
m = re.search(r"github\.com[/:]([^/]+)/([^/.]+)", remote)
if not m:
    sys.exit("origin remote is not a GitHub URL: " + remote)
owner, repo = m.group(1), m.group(2)
token = os.environ.get("GITHUB_TOKEN") or getpass.getpass("GitHub token (hidden): ").strip()

subprocess.call(["git", "add", "-A"])
if subprocess.call(["git", "diff", "--cached", "--quiet"]) != 0:
    subprocess.check_call(["git", "commit", "-m", "build " + time.strftime("%H:%M:%S")])
sha = sh("git", "rev-parse", "HEAD")
url = "https://x-access-token:%s@github.com/%s/%s.git" % (token, owner, repo)
print("Pushing...")
if subprocess.call(["git", "push", url, "HEAD:main"]) != 0:
    sys.exit("Push failed (check username/repo/token).")

def api(path, raw=False):
    req = urllib.request.Request("https://api.github.com" + path, headers={
        "Authorization": "Bearer " + token, "Accept": "application/vnd.github+json"})
    with urllib.request.urlopen(req, timeout=60) as r:
        return r.read() if raw else json.load(r)

print("Waiting for the build (a few minutes)...")
run = None
deadline = time.time() + 20 * 60
while time.time() < deadline:
    runs = api("/repos/%s/%s/actions/runs?head_sha=%s" % (owner, repo, sha))["workflow_runs"]
    if runs:
        run = runs[0]
        print("  status:", run["status"])
        if run["status"] == "completed":
            break
    time.sleep(15)
if not run or run["status"] != "completed":
    sys.exit("Timed out. Check the Actions tab on GitHub.")
if run["conclusion"] != "success":
    sys.exit("Build failed: %s\nOpen that page, copy the red error and send it to Claude." % run["html_url"])

arts = api("/repos/%s/%s/actions/runs/%s/artifacts" % (owner, repo, run["id"]))["artifacts"]
if not arts:
    sys.exit("No artifact found.")

class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, *a, **k):
        return None
req = urllib.request.Request(arts[0]["archive_download_url"], headers={"Authorization": "Bearer " + token})
try:
    urllib.request.build_opener(NoRedirect).open(req, timeout=60)
    sys.exit("Unexpected response while downloading.")
except urllib.error.HTTPError as e:
    loc = e.headers.get("Location")
    if not loc:
        raise
data = urllib.request.urlopen(loc, timeout=300).read()

out_dir = os.path.expanduser("~/storage/downloads")
if not os.path.isdir(out_dir):
    out_dir = os.path.expanduser("~")
with zipfile.ZipFile(io.BytesIO(data)) as z:
    name = [n for n in z.namelist() if n.endswith(".apk")][0]
    dest = os.path.join(out_dir, "BigClock.apk")
    with open(dest, "wb") as f:
        f.write(z.read(name))
print("APK saved:", dest)
subprocess.call(["termux-open", dest])
