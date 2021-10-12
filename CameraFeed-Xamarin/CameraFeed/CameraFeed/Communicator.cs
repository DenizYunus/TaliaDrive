using System;
using System.Collections.Generic;
using System.Text;

namespace CameraFeed
{
    public static class Communicator
    {
        public static bool takePhotoActivated = false;
        public static bool recordVideoActivated = false;
        public static int frame = 0;

        public static string ipAdress = "";
    }
}
