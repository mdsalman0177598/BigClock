BIG CLOCK — iPhone Tall Clock Patch

এই ZIP-এ শুধু দুটি Java source file আছে। এটি সম্পূর্ণ Android project নয়।

যে ফাইল দুটি প্রতিস্থাপন করতে হবে:
1) app/src/main/java/com/example/bigclock/ClockWallpaperService.java
2) app/src/main/java/com/example/bigclock/MainActivity.java

তারপর আগের মতো GitHub Actions দিয়ে APK build করুন।

নতুন বৈশিষ্ট্য:
- 24-hour সময় (যেমন 13:25), এক লাইনে বিশাল লম্বা ডিজিট
- তারিখ সময়ের উপরে
- DARK GLASS: কালো ব্যাকগ্রাউন্ডে বড় ধূসর ডিজিট
- OCEAN GLASS: সমুদ্র-অনুপ্রাণিত ব্যাকগ্রাউন্ডে স্বচ্ছ সাদা ডিজিট
- দুই স্টাইল আলাদা Apply বোতাম থেকে নির্বাচন করা যায়
- Live Wallpaper দৃশ্যমান থাকলে প্রতি সেকেন্ডে সময় আপডেট হয়

নোট: Android-এর Live Wallpaper preview থেকে Home/Lock screen নির্বাচন ফোনের সফটওয়্যারের ওপর নির্ভর করে।
