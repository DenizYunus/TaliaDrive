using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace CameraFeed
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class LoginPage : ContentPage
    {
        public LoginPage()
        {
            InitializeComponent();
        }

        public void GoToCameraPage(object sender, EventArgs e)
        {
            Communicator.name = NameInput.Text;

            Navigation.PushAsync(new MainPage());
        }

        public void GoToGalleryPage(object sender, EventArgs e)
        {
            Communicator.name = NameInput.Text;

            Navigation.PushAsync(new GalleryPage());
        }
    }
}