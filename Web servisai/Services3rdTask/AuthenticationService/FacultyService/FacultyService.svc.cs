using AuthenticationService.Authentication;
using AuthenticationService.Database;
using AuthenticationService.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Security.Permissions;
using System.Security.Principal;
using System.ServiceModel;
using System.ServiceModel.Activation;
using System.ServiceModel.Channels;
using System.Text;

namespace AuthenticationService
{
    [AspNetCompatibilityRequirements(RequirementsMode =
          AspNetCompatibilityRequirementsMode.Allowed)]
    public class FacultyService : IFacultyService
    {

        [PrincipalPermission(SecurityAction.Demand, Role = "Read")]
        public IEnumerable<Models.TeacherSetDTO> FindTeachers(string partialName)
        {
            Database.FacultyEntities db = new FacultyEntities();
            return db.TeacherSets.Where(x => x.Name.ToLower().Contains(partialName.ToLower()) ||
                                     x.Surname.ToLower().Contains(partialName.ToLower()) ||
                                     x.MidleName.ToLower().Contains(partialName.ToLower())).Select(x => new TeacherSetDTO()
                                     {
                                         Email = x.Email,
                                         Id = x.Id,
                                         MidleName = x.MidleName,
                                         MobilePhone = x.MobilePhone,
                                         Name = x.Name,
                                         StudySubjectSetIDS = x.StudySubjectSets.Select(subject => subject.Id),
                                         Surname = x.Surname,
                                         WorkPhone = x.WorkPhone
                                     }).Take(20);
        }

        [PrincipalPermission(SecurityAction.Demand, Role = "Read")]
        public IEnumerable<Models.StudySubjectDTO> FindSubjectsByTeacher(string partialTeacherName)
        {
            Database.FacultyEntities db = new FacultyEntities();
            var subjectsIDs = FindTeachers(partialTeacherName).Select(x => x.Id);
            return db.StudySubjectSets.Where(x => subjectsIDs.Contains(x.Id)).Select(x => new StudySubjectDTO()
            {
                AutumOrSpring = x.AutumOrSpring,
                Form = x.Form,
                Id = x.Id,
                Name = x.Name,
                Teacher_Id = x.Teacher_Id,
                ProgramSubjectsIds = x.ProgramSubjectsSets.Select(subject => subject.Id)
            }).Take(20);
        }

        [PrincipalPermission(SecurityAction.Demand, Role = "Write")]
        public bool AddTeacher(Models.TeacherSetDTO teacherToAdd)
        {
            Database.FacultyEntities db = new FacultyEntities();
            db.TeacherSets.Add(new TeacherSet()
            {
                Email = teacherToAdd.Email,
                MidleName = teacherToAdd.MidleName,
                MobilePhone = teacherToAdd.MobilePhone,
                Name = teacherToAdd.Name,
                StudySubjectSets = db.StudySubjectSets.Where(subject => teacherToAdd.StudySubjectSetIDS.Contains(subject.Id)).ToList(),
                Surname = teacherToAdd.Surname,
                WorkPhone = teacherToAdd.WorkPhone
            });

            if (db.SaveChanges() > 0)
            {
                return true;
            }
            return false;
        }

        [PrincipalPermission(SecurityAction.Demand, Role = "Delete")]
        public bool DeleteTeacher(int teacherID)
        {
            Database.FacultyEntities db = new FacultyEntities();
            var foundTeacher = db.TeacherSets.FirstOrDefault(x => x.Id == teacherID);
            if (foundTeacher == null)
            {
                return false;
            }

            db.TeacherSets.Remove(foundTeacher);
            return true;
        }

        [PrincipalPermission(SecurityAction.Demand, Role = "Super")]
        public bool AddUSer(string name, string password, IEnumerable<Faculty.Roles> roles)
        {
            // This function will add a user to our database

            // First create a new Guid for the user. This will be unique for each user
            Guid userGuid = System.Guid.NewGuid();

            // Hash the password together with our unique userGuid
            string hashedPassword = Security.HashSHA1(password + userGuid.ToString());

            Database.FacultyEntities db = new FacultyEntities();

            db.UserSets.Add(new Database.UserSet()
            {
                Name = name,
                PasswordHash = hashedPassword,
                UserGuid = userGuid.ToString(),
                RolesID = Faculty.ConvertRolesToInt(roles)
            });

            db.SaveChanges();

            return true;
        }



    }

}
