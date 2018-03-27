using AuthenticationService.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Text;

namespace AuthenticationService
{
    // NOTE: You can use the "Rename" command on the "Refactor" menu to change the interface name "IFacultyService" in both code and config file together.
    [ServiceContract]
    public interface IFacultyService
    {
        
        [OperationContract]
        IEnumerable<TeacherSetDTO> FindTeachers(string partialName);

        [OperationContract]
        IEnumerable<StudySubjectDTO> FindSubjectsByTeacher(string partialTeacherName);

        [OperationContract]        
        bool AddTeacher(TeacherSetDTO teacherToAdd);

        [OperationContract]        
        bool DeleteTeacher(int teacherID);

        [OperationContract]
        bool AddUSer(string name, string password, IEnumerable<Faculty.Roles> roles);
    }
}
