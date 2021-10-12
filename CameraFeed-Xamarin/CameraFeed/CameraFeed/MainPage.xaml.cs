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
            if (ipText.Text?.Length > 5)
            {
                Communicator.ipAdress = ipText.Text;
                Communicator.takePhotoActivated = true;
            }
            var animation = new Animation(v => take_photo_button.Scale = v, 1, 1.3);
            animation.Commit(this, "ScaleIt", length: 150, easing: Easing.Linear,
                finished: (v, c) => take_photo_button.Scale = 1, repeat: () => false);
            animation = new Animation(v => take_photo_button.Scale = v, 1.3, 1.0);
            animation.Commit(this, "ScaleIt", length: 150, easing: Easing.Linear,
                finished: (v, c) => take_photo_button.Scale = 1, repeat: () => false);
        }

        public void StartRecordingButton(object sender, EventArgs e)
        {
            Communicator.recordVideoActivated = true;
            record_video_button.Text = "Stop Recording";
            record_video_button.Clicked -= StartRecordingButton;
            record_video_button.Clicked += StopRecordingButton;
            take_photo_button.IsVisible = false;
        }

        public void StopRecordingButton(object sender, EventArgs e)
        {
            Communicator.recordVideoActivated = true;
            record_video_button.Text = "Start Recording";
            record_video_button.Clicked += StartRecordingButton;
            record_video_button.Clicked -= StopRecordingButton;
            take_photo_button.IsVisible = true;
        }
    }
}
