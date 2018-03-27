using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace AuthenticationService.Models
{
    [DataContract]
    public class Faculty
    {
        
        public enum Roles { Read = 1, Write = 2, Delete = 4, Super = 8 };

        public static int ConvertRolesToInt(IEnumerable<Roles> roles)
        {
            var rolesID = 0;

            if (roles.Contains(Roles.Read))
            {
                rolesID |= 1;
            }

            if (roles.Contains(Roles.Write))
            {
                rolesID |= 2;
            }
            if (roles.Contains(Roles.Delete))
            {
                rolesID |= 4;
            }
            if (roles.Contains(Roles.Super))
            {
                rolesID |= 8;
            }
            return rolesID;
        }

        public static IEnumerable<Roles> ConvertIntToRoles(int rolesID)
        {
            var roles = new List<Roles>();


            if ((1 & rolesID) > 0)
            {
                roles.Add(Roles.Read);
            }
            if ((2 & rolesID) > 0)
            {
                roles.Add(Roles.Write);
            }
            if ((4 & rolesID) > 0)
            {
                roles.Add(Roles.Delete);
            }
            if ((8 & rolesID) > 0)
            {
                roles.Add(Roles.Super);
            }

            return roles;
        }

        public static Roles StringToRole(string roleName)
        {
            roleName = roleName.ToLower().Trim();
            if (roleName == "read")
            {
                return Roles.Read;
            }
            if (roleName == "write")
            {
                return Roles.Write;
            }
            if (roleName == "delete")
            {
                return Roles.Delete;
            }
            if (roleName == "super")
            {
                return Roles.Super;
            }
            throw new NotImplementedException("wrong roles implementation (to Role)");
        }

        public static string RoleToString(Roles role)
        {
            switch (role)
            {
                case Roles.Read:
                    return "Read";
                case Roles.Write:
                    return "Write";
                case Roles.Delete:
                    return "Delete";
                case Roles.Super:
                    return "Super";
                default:
                    throw new NotImplementedException("wrong roles implementation (to String)");
            }

        }
    }
}