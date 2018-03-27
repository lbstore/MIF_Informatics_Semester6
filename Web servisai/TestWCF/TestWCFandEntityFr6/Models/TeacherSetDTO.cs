using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;
using WebServicesPresentation;

namespace WebServicesPresentation.Models
{
    [DataContract]
    public class TeacherSetDTO
    {
        public TeacherSetDTO()
        {
             this.StudySubjectSetIDS = new HashSet<int>();
        }

        public TeacherSetDTO(TeacherSet teacher)
            : base()
        {
            this.Id = teacher.Id;
            this.MidleName = teacher.MidleName;
            this.MobilePhone = teacher.MobilePhone;
            this.Name = teacher.Name;
            this.Surname = teacher.Surname;
            this.WorkPhone = teacher.WorkPhone;
            this.Email = teacher.Email;
            StudySubjectSetIDS = teacher.StudySubjectSets.Select(x => x.Id);
        }

        [DataMember(IsRequired=true)]
        public int Id { get; set; }
        [DataMember]
        public string Name { get; set; }
        [DataMember]
        public string MidleName { get; set; }
        [DataMember]
        public string Surname { get; set; }
        [DataMember]
        public string Email { get; set; }
        [DataMember]
        public string WorkPhone { get; set; }
        [DataMember]
        public string MobilePhone { get; set; }
        [DataMember]
        public virtual IEnumerable<int> StudySubjectSetIDS { get; set; }
    }
}