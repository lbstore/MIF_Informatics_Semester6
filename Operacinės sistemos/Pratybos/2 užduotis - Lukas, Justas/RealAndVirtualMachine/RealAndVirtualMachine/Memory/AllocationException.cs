using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace RealAndVirtualMachine.Memory
{
    [Serializable]
    public class AllocationException : Exception
    {
         public AllocationException ()
    {}

    public AllocationException (string message) 
        : base(message)
    {}

    public AllocationException (string message, Exception innerException)
        : base (message, innerException)
    {}

    protected AllocationException(SerializationInfo info, StreamingContext context)
        : base (info, context)
    {}

    }
}
