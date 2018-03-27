using AuthenticationService.Database;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace AuthenticationService.Models
{
    [DataContract]
    public class StudyProgramSetDTO
    {

        public StudyProgramSetDTO()
        {
            this.StudySubjectsSetIds = new HashSet<int?>();
        }

        public StudyProgramSetDTO(StudyProgramSet studyProgram)
            : this()
        {
            this.Id = studyProgram.Id;
            this.Name = studyProgram.Name;
            this.Degree = studyProgram.Degree;
            StudySubjectsSetIds = studyProgram.ProgramSubjectsSets.Select(x => x.StudySubject_Id);

        }

        [DataMember]
        public int Id { get; set; }
        [DataMember]
        public string Name { get; set; }
        [DataMember]
        public string Degree { get; set; }

        [DataMember]
        public virtual IEnumerable<int?> StudySubjectsSetIds { get; set; }
    }
}