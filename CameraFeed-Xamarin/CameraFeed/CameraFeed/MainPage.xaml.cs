using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;

namespace CameraFeed
{
    // Learn more about making custom code visible in the Xamarin.Forms previewer
    // by visiting https://aka.ms/xamarinforms-previewer
    [DesignTimeVisible(false)]
    public partial class MainPage : CameraFeed
    {
        public MainPage()
        {
            InitializeComponent();
        }

        public void TakePicture(object sender, EventArgs e)
        {
            Communicator.ipAdress = ipText.Text;
            Communicator.takePhotoActivated = true;
        }

        public void StartRecordingButton(object sender, EventArgs e)
        {

        }

        public void StopRecordingButton(object sender, EventArgs e)
        {

        }
    }
}
