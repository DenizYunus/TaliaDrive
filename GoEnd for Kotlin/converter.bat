echo off
set arg1=%1
shift
shift

ffmpeg -r 4 -start_number 0 -i "c:\Users\deniz\Documents\GoProjects\TestProject\video-temp-images\%arg1%_%%d.jpg" -c:v libx264 -vf "fps=30,format=yuv420p" c:\Users\deniz\Documents\GoProjects\TestProject\final-videos\%arg1%_vid.mp4