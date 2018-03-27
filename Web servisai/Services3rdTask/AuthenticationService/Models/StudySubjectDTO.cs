using AuthenticationService.Database;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace AuthenticationService.Models
{
    [DataContract]
    public class StudySubjectDTO
    {

        public StudySubjectDTO()
        {
            this.ProgramSubjectsIds = 
                new HashSet<int>();
        }

        [DataMember]
        public int Id { get; set; }
        [DataMember]
        public string Name { get; set; }
        [DataMember]
        public string AutumOrSpring { get; set; }
        [DataMember]
        public string Form { get; set; }
        [DataMember]
        public int Teacher_Id { get; set; }
        [DataMember]
        public virtual IEnumerable<int> 
            ProgramSubjectsIds { get; set; }        
    }
}