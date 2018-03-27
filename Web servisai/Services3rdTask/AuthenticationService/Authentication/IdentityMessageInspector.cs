using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel.Dispatcher;
using System.ServiceModel.Channels;
using System.Net;
using System.IO;
using System.ServiceModel;
using System.Security.Principal;
using System.Web.Security;
using System.Threading;
using System.Collections;


namespace AuthenticationService
{
    public class IdentityMessageInspector : IDispatchMessageInspector
    {
        public object AfterReceiveRequest(ref Message request, System.ServiceModel.IClientChannel channel, System.ServiceModel.InstanceContext instanceContext)
        {
            var messageProperty = (HttpRequestMessageProperty)
                OperationContext.Current.IncomingMessageProperties[HttpRequestMessageProperty.Name];
            string cookie = messageProperty.Headers.Get("Set-Cookie");
            if (cookie == null) // Check for another Message Header - SL applications
            {
                cookie = messageProperty.Headers.Get("Cookie");
            }
            if (cookie == null)
                cookie = string.Empty;

            var authCookies = new List<string>();
            var cookieValuePairs = cookie.Split(',', ';').Where(x => x.ToLower().Contains(FormsAuthentication.FormsCookieName.ToLower()));
            foreach (var pair in cookieValuePairs)
            {                
                var splitted = pair.Split('=');
                var value = splitted.Skip(1).First();
                authCookies.Add(value);
            }

            string encryptedTicket = string.Empty;

            // Set User Name from cookie
            if (authCookies.Count > 0)
                encryptedTicket = authCookies.First().ToString();

            FormsAuthenticationTicket ticket = null;
            string userName = string.Empty;
            string roles = string.Empty;

            // Decrypt
            if (!string.IsNullOrEmpty(encryptedTicket))
            {
                ticket = FormsAuthentication.Decrypt(encryptedTicket);
                userName = ticket.Name;
                roles = ticket.UserData;
            }

            // Set Thread Principal to User Name
            if (!string.IsNullOrEmpty(userName))
            {
                CustomIdentity customIdentity = new CustomIdentity();
                GenericPrincipal threadCurrentPrincipal = new GenericPrincipal(customIdentity, roles.Split(',').Select(x => x.Trim()).ToArray());
                customIdentity.IsAuthenticated = true;
                customIdentity.Name = userName;
                Thread.CurrentPrincipal = threadCurrentPrincipal;
            }

            return null;
        }

        private string[] GetRoles(string value)
        {
            if (!string.IsNullOrEmpty(value))
            {
                List<string> roles = new List<string>();

                int ix = 0;
                foreach (string item in value.Split(';'))
                {
                    if (ix > 0)
                        if (item.Trim().Length > 0)
                            roles.Add(item);

                    ix++;
                }

                return roles.ToArray<string>();
            }

            return new string[0];
        }

        private string GetUserName(string value)
        {
            if (!string.IsNullOrEmpty(value))
            {
                foreach (string item in value.Split(';'))
                    return item;
            }

            return string.Empty;
        }

        public void BeforeSendReply(ref Message reply, object correlationState)
        {

        }
    }
}
