using System;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using RealAndVirtualMachine.Annotations;

namespace RealAndVirtualMachine.Memory
{
    public class Word
    {
        private readonly byte[] _data = new byte[4];


        /*        public Word(byte b1, byte b2, byte b3, byte b4)
                {
                    _data[0] = b1;
                    _data[1] = b2;
                    _data[2] = b3;
                    _data[3] = b4;                      
                }*/

        /*        public Word(char c1, char c2, char c3, char c4) : this((byte)c1,(byte)c2,(byte)c3,(byte)c4)
                {
            
                }*/

        private void SetValues(string line)
        {
            var str = line.ToUpper();

            var string2 = new string(' ', 4 - str.Length) + str;
            var chars = string2.ToUpper().ToCharArray();
            
            
            _data[0] = (byte)chars[0];
            _data[1] = (byte)chars[1];
            _data[2] = (byte)chars[2];
            _data[3] = (byte)chars[3];


        }

        public Word(int value)
            : this(value.ToString("X4"))
        {

        }

        public Word(string line)
        {
            SetValues(line);



        }

        public Word()
        {

        }

        public string GetString()
        {
            char[] temp = { (char)_data[0], (char)_data[1], (char)_data[2], (char)_data[3] };
            return new string(temp);
        }

        public int GetInt()
        {
            var str = GetString().TrimStart();
            return Convert.ToInt32(str, 16);
        }

        /* public int GetInt()
         {
             return ((_data[0] & 0xFF) << 24) | ((_data[1] & 0xFF) << 16)
             | ((_data[2] & 0xFF) << 8) | (_data[3] & 0xFF);
         }*/

        public void SetInt(int value)
        {
            _data[0] = (byte)((value >> 24) & 0xFF);
            _data[1] = (byte)((value >> 16) & 0xFF);
            _data[2] = (byte)((value >> 8) & 0xFF);
            _data[3] = (byte)(value & 0xFF);
        }
        /// <summary>
        ///  Word is made from 4 bytes, you can get value from any of those bytes seperatly
        /// </summary>
        /// <param name="index">Which bytes value return, index must be between 0 and 3</param>
        /// <returns></returns>
        public byte GetByte(int index)
        {
            if (index > 3 || index < 0)
            {
                throw new IndexOutOfRangeException("Index must be in range [0..3], tried to access byte at " + index);
            }
            return _data[index];
        }

        /// <summary>
        /// Word is made from 4 bytes, you can set value for any of those bytes seperatly 
        /// </summary>
        /// <param name="index">Which bytes value to set, index must be between 0 and 3</param>
        /// <param name="value">new bytes value to set</param>
        public void SetByte(int index, byte value)
        {
            if (index > 3 || index < 0)
            {
                throw new IndexOutOfRangeException("Index must be in range [0..3], tried to access byte at " + index);
            }
            _data[index] = value;
        }

        // Define the indexer, which will allow client code 
        // to use [] notation on the class instance itself.

        public byte this[int i]
        {
            get { return GetByte(i); }
            set { SetByte(i, value); }
        }


        public override string ToString()
        {
            return GetString();
        }
    }
}
