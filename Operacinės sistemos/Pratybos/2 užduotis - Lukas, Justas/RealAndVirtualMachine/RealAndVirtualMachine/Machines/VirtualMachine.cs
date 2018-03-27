using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.IO;
using System.Runtime.CompilerServices;
using System.Text;
using System.Windows.Shapes;
using RealAndVirtualMachine.Annotations;
using RealAndVirtualMachine.Memory;
using RealAndVirtualMachine.Properties;

namespace RealAndVirtualMachine.Machines
{
    public class VirtualMachine : INotifyPropertyChanged
    {


        #region VARIABLES AND PROPERTIES
        public string Name { get; set; }
        private enum Segments { DS, PS, SS };
        private enum Commands
        {
            ADD, SUB, MUL, DIV, WR, RD, CMP, LD, PT, JP, JE, JL, JG, FORK, STOP, PRTS, PRTN, PS, SD,
            PF, DA, FLUS,
            COPY
        }

        private readonly Dictionary<Segments, short> m_segmentsStartAddresses = new Dictionary<Segments, short>();
        private bool m_isFinished;
        private StringBuilder m_console = new StringBuilder();
        private static byte maxPID = 0;
        public PageTable PagesTable { get; private set; }
        private readonly RealMachine m_realMachine;
        private byte m_pid;
        private short m_dp;
        private short m_sp;
        private short m_pc;
        

        public StringBuilder Console
        {
            get { return m_console; }
            set
            {
                if (Equals(value, m_console)) return;
                m_console = value;
                OnPropertyChanged();
            }
        }

        public bool IsFinished
        {
            get { return m_isFinished; }
            private set
            {
                if (value.Equals(m_isFinished)) return;
                m_isFinished = value;
                OnPropertyChanged();
            }
        }

        // program counter - virtual address
        public short PC
        {
            get { return m_pc; }
            set
            {
                if (value == m_pc) return;
                m_pc = value;
                OnPropertyChanged();
            }
        }

        //stack pointer - points to data segment
        public short SP
        {
            get { return m_sp; }
            private set
            {
                if (value == m_sp) return;
                m_sp = value;
                OnPropertyChanged();
            }
        }

        //data pointer - points to data segment
        public short DP
        {
            get { return m_dp; }
            private set
            {
                if (value == m_dp) return;
                m_dp = value;
                OnPropertyChanged();
            }
        }

        //process id
        public byte PID
        {
            get { return m_pid; }
            private set
            {
                OnPropertyChanged("PID");
                m_pid = value;

            }
        }

        private void AppendToConsole(string line)
        {
            if (string.IsNullOrEmpty(line))
            {
                return;
            }
            if (line.ToUpper().Contains("\\N"))
            {
                line = line.Replace("\\N", "\n");
            }
            Console.Append(line);
            OnPropertyChanged("Console");
        }
        private void AppendLineToConsole(string line)
        {
            Console.AppendLine(line);
            OnPropertyChanged("Console");
        }

        #endregion

        public VirtualMachine(VirtualMachine oldVirtualMachine)
        {
            this.DP = oldVirtualMachine.DP;
            this.PC = oldVirtualMachine.PC;
            this.SP = oldVirtualMachine.SP;
            this.DP = oldVirtualMachine.DP;
            this.Console = new StringBuilder(oldVirtualMachine.Console.ToString());
            this.IsFinished = oldVirtualMachine.IsFinished;
            this.Name = oldVirtualMachine.Name;
            this.m_realMachine = oldVirtualMachine.m_realMachine;

            maxPID++;
            PID = maxPID;
            this.PagesTable = new PageTable(oldVirtualMachine.PagesTable);
        }

        public VirtualMachine(RealMachine realMachine, short pc = 0, short sp = 0)
        {
            m_segmentsStartAddresses.Add(Segments.PS, 0);
            m_segmentsStartAddresses.Add(Segments.DS, (short)((Settings.Default.ProgramSegmentPagesCount) * Settings.Default.PageSize));
            m_segmentsStartAddresses.Add(Segments.SS, (short)((Settings.Default.ProgramSegmentPagesCount + Settings.Default.DataSegmentPagesCount) * Settings.Default.PageSize));

            this.m_realMachine = realMachine;
            PagesTable = new PageTable(m_realMachine);
            if (sp == 0)
            {
                SP = m_segmentsStartAddresses[Segments.SS];
            }
            else
            {
                SP = sp;
            }
            PC = pc;
            DP = m_segmentsStartAddresses[Segments.DS];
            maxPID++;
            PID = maxPID;
        }

        #region MEMORY i/O COMMANDS

        private void checkLine(string line)
        {
            if (line == null)
            {
                throw new ProgramContractException("program file ended withoud full contract implementation");
            }
            if (line.Length > 4)
            {
                throw new ProgramContractException("program line can not be longer than 4 symbols");
            }
        }

        public void LoadProgramToMemmory(string programFilePath)
        {
            StreamReader file = new StreamReader(@programFilePath);
            try
            {


                //first line must be $AMJ
                var line = file.ReadLine();
                checkLine(line);
                if (!line.ToUpper().Equals(Settings.Default.ProgramCodeStartSymbol))
                {
                    throw new ProgramContractException("Program must start with $AMJ sign");
                }

                line = file.ReadLine();
                checkLine(line);
                Name = line;

                int programPointer = 0;
                var maxProgramPointerValue = Settings.Default.ProgramSegmentPagesCount * Settings.Default.PageSize - 1;
                bool stopReadingProgramCode = false;
                do
                {
                    line = file.ReadLine();
                    checkLine(line);


                    if (programPointer > maxProgramPointerValue)
                    {
                        throw new ProgramContractException("program code is too long");
                    }

                    if (!line.ToUpper().Equals(Settings.Default.ProgramDataStartSymbol))
                    {
                        var physicalAddress = PagesTable.GetPhysicalAddress(programPointer);
                        m_realMachine.WriteMem(physicalAddress, new Word(line));
                    }
                    else
                    {
                        stopReadingProgramCode = true;
                    }

                    programPointer++;
                } while (!stopReadingProgramCode);


                var dataPointer = maxProgramPointerValue + 1;
                var maxDataPointerValue = dataPointer + Settings.Default.DataSegmentPagesCount * Settings.Default.PageSize;
                var stopReadingDataCode = false;
                do
                {
                    line = file.ReadLine();
                    checkLine(line);
                    if (dataPointer > maxDataPointerValue)
                    {
                        throw new ProgramContractException("data code is too long");
                    }

                    if (!line.ToUpper().Equals(Settings.Default.ProgramEndSymbol))
                    {
                        m_realMachine.WriteMem(PagesTable.GetPhysicalAddress(dataPointer), new Word(line));
                    }
                    else
                    {
                        stopReadingDataCode = true;
                    }
                    dataPointer++;
                } while (!stopReadingDataCode);
            }
            catch (Exception e)
            {
                PagesTable.DeallocateAllPages();
                throw;
            }
            finally
            {
                file.Close();
            }
        }

        private Word ReadMem(int virtualAddress)
        {
            return m_realMachine.ReadMem(PagesTable.GetPhysicalAddress(virtualAddress));
        }

        private void WriteMem(int virtualAddress, Word word)
        {
            m_realMachine.WriteMem(PagesTable.GetPhysicalAddress(virtualAddress), word);
        }

        private void Push(int value)
        {
            Push(new Word(value));
        }

        private void Push(Word word)
        {
            WriteMem(SP, word);
            SP++;
        }

        private Word GetData(int dataLineNumber)
        {
            if (dataLineNumber < 0 ||
                dataLineNumber > Settings.Default.PageSize * Settings.Default.DataSegmentPagesCount - 1)
            {
                throw new ProgramContractException("Tried to get data that is out of bounds", new IndexOutOfRangeException());
            }
            return ReadMem(DP + dataLineNumber);
        }

        private Word Pop()
        {
            SP--;
            var value = ReadMem(SP);
            WriteMem(SP, new Word());
            return value;
        }

        #endregion

        #region INSTRUCTIONS
        public void DoNextInstruction()
        {
            if (IsFinished)
            {
                return;
            }

            string command = m_realMachine.ReadMem(PagesTable.GetPhysicalAddress(PC)).GetString().TrimStart();
            PC++;

            if (command.StartsWith(Commands.ADD.ToString()))
            {
                DoAdd();
                return;
            }

            if (command.StartsWith(Commands.SUB.ToString()))
            {
                DoSub();
                return;
            }
            if (command.StartsWith(Commands.CMP.ToString()))
            {
                DoCmp();
                return;
            }
            if (command.StartsWith(Commands.MUL.ToString()))
            {
                DoMul();
                return;
            }
            if (command.StartsWith(Commands.DIV.ToString()))
            {
                DoDiv();
                return;
            }
            if (command.StartsWith(Commands.WR.ToString()))
            {
                DoWR(command);
                return;
            }
            if (command.StartsWith(Commands.RD.ToString()))
            {
                DoRd(command);
                return;
            }
            if (command.StartsWith(Commands.LD.ToString()))
            {
                DoLD(command);
                return;
            }
            if (command.StartsWith(Commands.PT.ToString()))
            {
                DoPT(command);
                return;
            }
            if (command.StartsWith(Commands.JP.ToString()))
            {
                DoJp(command);
                return;
            }
            if (command.StartsWith(Commands.JE.ToString()))
            {
                DoJe(command);
                return;
            }
            if (command.StartsWith(Commands.JL.ToString()))
            {
                DoJL(command);
                return;
            }
            if (command.StartsWith(Commands.JG.ToString()))
            {
                DoJG(command);
                return;
            }
            if (command.StartsWith(Commands.FORK.ToString()))
            {
                DoFork();
                return;
            }
            // flush stack - removes one element from stack
            if (command.StartsWith(Commands.FLUS.ToString()))
            {
                DoFlus();
                return;
            }

            if (command.StartsWith(Commands.COPY.ToString()))
            {
                DoCopy();
                return;
            }

            if (command.StartsWith(Commands.STOP.ToString()))
            {
                DoStop();
                return;
            }
            //print symbol
            if (command.StartsWith(Commands.PRTS.ToString()))
            {
                DoPRTS();
                return;
            }
            //print number
            if (command.StartsWith(Commands.PRTN.ToString()))
            {
                DoPRTN();
                return;
            }

            //prints until & symbol is found - print buffer
            if (command.StartsWith(Commands.PF.ToString()))
            {
                DoPF(command);
                return;
            }
            if (command.StartsWith(Commands.DA.ToString()))
            {
                DoDA(command);
                return;
            }


            //STACK DATA 
            if (command.StartsWith(Commands.SD.ToString()))
            {
                DoSD(command);
                return;
            }

            //PUSH
            if (command.StartsWith(Commands.PS.ToString()))
            {
                DoPs(command);
                return;
            }


            if (String.IsNullOrWhiteSpace(command))
            {
                throw new ProgramContractException("Command instruction is blank line");
            }
            throw new ProgramContractException("Cound not find instruction: " + command);
        }


        private void DoCopy()
        {
            var value = Pop();
            Push(value);
            Push(value);
        }

        private void DoFlus()
        {
            Pop();
        }

        //stack data address
        private void DoDA(string command)
        {
            Push(m_segmentsStartAddresses[Segments.DS] + Convert.ToInt32(command.Substring(2, 2), 16));
        }

        private void DoPRTN()
        {
            AppendToConsole(Pop().GetInt() + "");           
        }


        private void DoPRTS()
        {
            AppendToConsole(Pop().GetString());    
        }

        //stack data
        private void DoSD(string command)
        {
            var address = m_segmentsStartAddresses[Segments.DS] + Convert.ToInt32(command.Substring(2, 2), 16);            
            if (address < m_segmentsStartAddresses[Segments.DS] || address > m_segmentsStartAddresses[Segments.SS] - 1)
            {
                throw new ProgramContractException("Wrong WR command use, can write only to data segment");
            }
            Push(ReadMem(address));
        }

        //print buffer until $ symbol is found
        private void DoPF(string command)
        {
            var address = Convert.ToInt32(command.Substring(2, 2), 16);            
            string line;
            do
            {                
                line = ReadMem(address).GetString();
                address++;
                if (!line.Contains("$"))
                {                                        
                    AppendToConsole(line);
                }
            } while (!line.Contains("$"));

            AppendToConsole(line.Substring(0,line.IndexOf("$", System.StringComparison.Ordinal)));
            
        }

        private void DoFork()
        {
            var vm = m_realMachine.ForkVirtualMachine(this);
            Push(vm.PID);
            vm.Push(0);

        }

        private void DoStop()
        {
            IsFinished = true;
        }

        #region JUMP COMMANDS
        private void DoJp(string command)
        {
            var address = Convert.ToInt32(command.Substring(2, 2), 16);
            if (address < m_segmentsStartAddresses[Segments.PS] || address > m_segmentsStartAddresses[Segments.DS] - 1)
            {
                throw new ProgramContractException("Wrong JP command use, can only jump within program code segment");
            }
            PC = (short)address;
        }

        private void DoJe(string command)
        {
            var address = Convert.ToInt32(command.Substring(2, 2), 16);
            if (address < m_segmentsStartAddresses[Segments.PS] || address > m_segmentsStartAddresses[Segments.DS] - 1)
            {
                throw new ProgramContractException("Wrong JE command use, can only jump within program code segment");
            }
            if (Pop().GetInt() == 1)
            {
                PC = (short)address;
            }
        }

        private void DoJL(string command)
        {
            var address = Convert.ToInt32(command.Substring(2, 2), 16);
            if (address < m_segmentsStartAddresses[Segments.PS] || address > m_segmentsStartAddresses[Segments.DS] - 1)
            {
                throw new ProgramContractException("Wrong JL command use, can only jump within program code segment");
            }
            if (Pop().GetInt() == 0)
            {
                PC = (short)address;
            }
        }

        private void DoJG(string command)
        {
            var address = Convert.ToInt32(command.Substring(2, 2), 16);
            if (address < m_segmentsStartAddresses[Segments.PS] || address > m_segmentsStartAddresses[Segments.DS] - 1)
            {
                throw new ProgramContractException("Wrong JG command use, can only jump within program code segment");
            }
            if (Pop().GetInt() == 2)
            {
                PC = (short)address;
            }
        }

        #endregion

        #region I/O COMMANDS
        private void DoRd(string command)
        {
            var address = Convert.ToInt32(command.Substring(2, 2), 16);
            Push(ReadMem(address));
        }

        private void DoWR(string command)
        {
            var address = Convert.ToInt32(command.Substring(2, 2), 16);
            if (address < m_segmentsStartAddresses[Segments.DS] || address > m_segmentsStartAddresses[Segments.SS] - 1)
            {
                throw new ProgramContractException("Wrong WR command use, can write only to data segment");
            }
            WriteMem(address, Pop());
        }
        #endregion

        #region Aritmetics
        private void DoAdd()
        {
            var variable1 = Pop();
            var variable2 = Pop();
            var result = variable1.GetInt() + variable2.GetInt();
            Push(new Word(result));
        }

        private void DoSub()
        {
            var variable1 = Pop();
            var variable2 = Pop();
            var result = variable1.GetInt() - variable2.GetInt();
            Push(new Word(result));
        }

        private void DoMul()
        {
            var variable1 = Pop();
            var variable2 = Pop();
            var result = variable1.GetInt() * variable2.GetInt();
            Push(new Word(result));
        }

        private void DoDiv()
        {
            var variable1 = Pop();
            var variable2 = Pop();
            var result = variable1.GetInt() / variable2.GetInt();
            Push(new Word(result));
        }

        private void DoCmp()
        {
            var value1 = Pop().GetInt();
            var value2 = Pop().GetInt();
            if (value1 > value2)
            {
                Push(0);
            }
            if (value1 == value2)
            {
                Push(1);
            }
            if (value1 < value2)
            {
                Push(2);
            }
        }
        #endregion

        #region Stack

        private void DoLD(string command)
        {
            var address = Convert.ToInt32(command.Substring(2, 2), 16);
            var value = ReadMem(address);
            Push(value);
        }

        private void DoPT(string command)
        {
            var address = Convert.ToInt32(command.Substring(2, 2), 16);
            var value = Pop();
            WriteMem(address, value);
        }

        //PSxy - xy as hex number will be put into stack
        private void DoPs(string command)
        {
            var value = Convert.ToInt32(command.Substring(2, 2), 16);
            Push(new Word(value));
        }
        #endregion
        #endregion


        #region CHANGES NOTIFICATION
        public event PropertyChangedEventHandler PropertyChanged;

        [NotifyPropertyChangedInvocator]
        protected virtual void OnPropertyChanged([CallerMemberName] string propertyName = null)
        {
            PropertyChangedEventHandler handler = PropertyChanged;
            if (handler != null) handler(this, new PropertyChangedEventArgs(propertyName));
        }

        public void ReleaseResources()
        {
            PagesTable.DeallocateAllPages();
        }
        #endregion
    }
}
