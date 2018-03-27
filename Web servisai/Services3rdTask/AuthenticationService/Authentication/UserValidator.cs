using AuthenticationService.Authentication;
using AuthenticationService.Database;
using AuthenticationService.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel;
using System.Web;

namespace AuthenticationService
{
    public class UserValidator
    {
     
        
        public bool IsUserValid(string userName, string password, out IEnumerable<Faculty.Roles> roles)
        {
            roles = new List<Faculty.Roles>();

            FacultyEntities db = new Database.FacultyEntities();

            var foundUser = db.UserSets.FirstOrDefault(x => x.Name.ToLower().Equals(userName.ToLower()));
            if (foundUser == null)
            {
                return false;
            }
            var hashedPass = Security.HashSHA1(password + foundUser.UserGuid);

            if (hashedPass == foundUser.PasswordHash)
            {
                roles = Faculty.ConvertIntToRoles((int)foundUser.RolesID);
                return true;
            }
            return false;
            
        }

    }
}