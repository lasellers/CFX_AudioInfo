import java.io.FileInputStream;
import java.io.FileNotFoundException;
//import java.io.FilenameFilter;
import java.io.IOException;   
/**
     *
     *
     *
     * the READ class is a set of functions used to read in data types in a
     * simple uniform manner.
     * @author Lewis A. Sellers (min)
     */
    class READ {
        private FileInputStream fis = null;
        private int iFPosition = 0;
        private String lastFilename = "";
        private boolean EOF = false;
        
        /**
         * opens file by name of filename and readies it for reading.
         */
        boolean open(String filename) {
            lastFilename = filename;
            iFPosition = 0;
            EOF = false;
            
            try {
                fis = new FileInputStream(filename);
            }
            catch (FileNotFoundException e) {
            	//log_itemerror(log.iFILEDOESNOTEXIST, "File does not exist (" + filename + ").");
            	log.log_error(log.iFILEDOESNOTEXIST, "File does not exist (" + filename + ").");
                EOF = true;
            }
            catch (SecurityException e) {
            	//log_itemerror(log.iSECURITY, "Security on file (" + filename + ").");
            	log.log_error(log.iSECURITY, "Security on file (" + filename + ").");
                EOF = true;
            }
            
            return !EOF; //true if file opens, false if can not be
        }
        
        /**
         * closes current read file.
         */
        boolean close() {
            EOF = false;
            
            try {
                fis.close();
            }
            catch (IOException e) {
               //log_itemerror(log.iFILEDOESNOTEXIST, "File not closed properly.");
                log.log_error(log.iFILEDOESNOTEXIST, "File not closed properly.");
            }
            
            return !EOF; //1 if ok, 0 if error
        }
        
        /**
         *gets the current read position in the open file.
         */
        int getPosition() {
            return iFPosition;
        }
        
        /**
         *sets the position into the current open file.
         */
        void setPosition(int pos) throws EOFException {
            close();
            open(lastFilename);
            skip(pos);
        }
        
        /**
         *checks if end of file has been reached.
         *returns true if it has.
         */
        boolean isEOF() {
            return EOF;
        }
        
        /**
         * the fundamental BYTE reading method that all other methods use
         * to read data.
         * reads in one byte at a time from bufered input
         * @author Lewis A. Sellers (min)
         * @exception EOFException
         * @returns last byte read as an unsigned byte in char
         */
        short NEXTBYTE() throws EOFException {
            short ch;
            
            if (EOF)
                throw new EOFException();
            
            try {
                ch = (short) fis.read();
                if (ch < 0)
                    ch += 128;
                
                iFPosition++;
            }
            catch (IOException e) {
                EOF = true;
                throw new EOFException();
            }
            
            return ch;
        }
        
        /**
         * reads up to len bytes of data and returns as a java byte[] array.
         * @author Lewis A. Sellers (min)
         * @exception EOFException
         * @parm len number of bytes to read in
         * @returns byte[] array with the data that was read in
         */
        byte[] DATA(int len) throws EOFException {
            if (len <= 0)
                return null;
            
            int slen;
            byte[] buffer;
            
            try {
                buffer = new byte[len];
                slen = fis.read(buffer, 0, len);
                iFPosition += slen;
            }
            catch (IOException e) {
                EOF = true;
                buffer = null;
            }
            
            if (EOF)
                throw new EOFException();
            
            return buffer;
        }
        
        /**
         * reads in a null terminated ascii string and converts it to a java String type.
         * @author Lewis A. Sellers (min)
         * @exception EOFException
         * @returns the ascii data read as a String type
         */
        String STRING() throws EOFException {
            String str = "";
            short ch = 0;
            
            try {
                do {
                    ch = NEXTBYTE();
                    iFPosition++;
                    str += (char) ch;
                }
                while (ch != 0);
            }
            catch (EOFException e) {
                EOF = true;
            }
            
            if (EOF)
                throw new EOFException();
            
            return str;
        }
        
        /**
         * reads up to len bytes of ascii data and converts it to a java String type.
         * @author Lewis A. Sellers (min)
         * @exception EOFException
         *@parm len number of bytes to read in as a string
         * @returns the ascii data read as a String type
         */
        String STRING(int len) throws EOFException {
            if (len <= 0)
                return "";
            
            int slen;
            String buffer;
            
            try {
                byte[] bytebuffer = new byte[len];
                slen = fis.read(bytebuffer, 0, len);
                
                iFPosition += slen;
                buffer = new String(bytebuffer);
            }
            catch (IOException e) {
                buffer = "";
                EOF = true;
            }
            
            if (EOF)
                throw new EOFException();
            
            return buffer;
        }
        
        /**
         * skips n bytes further into current open file.
         * @author Lewis A. Sellers (min)
         * @exception EOFException
         *@parm len number of bytes to skip
         * @returns integer indicating number of bytes actually skipped
         */
        int skip(int len) throws EOFException {
            if (len <= 0)
                return 0;
            
            long slen;
            
            try {
                slen = fis.skip(len);
                iFPosition += slen;
            }
            catch (IOException e) {
                EOF = true;
                slen = 0;
            }
            
            if (EOF)
                throw new EOFException();
            
            return (int) slen;
        }
        
        /**
         * reads a big endian (motorola) byte from file stream
         * and returns a 16-bit char.
         * same as BYTE method.
         * @author Lewis A. Sellers (min)
         * @exception EOFException
         * @returns 8-bit unsigned byte as a short
         */
        short BYTE_BIGENDIAN() throws EOFException {
            return NEXTBYTE();
        }
        
        /**
         * reads a single unsigned byte from the file stream
         * and passes back a 16-bit char.
         * @author Lewis A. Sellers (min)
         * @exception EOFException
         * @returns 8-bit unsigned byte as a short
         */
        short BYTE() throws EOFException {
            return NEXTBYTE();
        }
        
        /**
         * reads a normal unsigned WORD from the file stream
         * and passes back a java signed integer.
         * @author Lewis A. Sellers (min)
         * @exception EOFException
         * @returns 16-bit word as an integer
         */
        int WORD() throws EOFException {
            int c1;
            int c2;
            c1 = NEXTBYTE();
            c2 = NEXTBYTE();
            
            return (c2 << 8) + c1;
        }
        
        /**
         * reads a normal unsigned bigendian WORD from the file stream
         * and passes back a java signed integer.
         * @author Lewis A. Sellers (min)
         * @exception EOFException
         * @returns 16-bit bigendian word as an integer
         */
        int WORD_BIGENDIAN() throws EOFException {
            int c1;
            int c2;
            c1 = NEXTBYTE();
            c2 = NEXTBYTE();
            
            return (c1 << 8) + c2;
        }
        
        /**
         * reads a normal unsigned DWORD from the file stream
         * and passes it back as a java signed long.
         * @author Lewis A. Sellers (min)
         * @exception EOFException
         * @returns 32-bit dword as a long
         */
        long DWORD() throws EOFException {
            long c1;
            long c2;
            long c3;
            long c4;
            c1 = NEXTBYTE();
            c2 = NEXTBYTE();
            c3 = NEXTBYTE();
            c4 = NEXTBYTE();
            
            return (c4 << 24) + (c3 << 16) + ( c2 << 8) + c1;
        }
        
        /**
         * reads a normal unsigned bigendian DWORD from the file stream
         * and passes it back as a java signed long.
         * @author Lewis A. Sellers (min)
         * @exception EOFException
         * @returns 32-bit bigendian dword as a long
         */
        long DWORD_BIGENDIAN() throws EOFException {
            long c1;
            long c2;
            long c3;
            long c4;
            c1 = NEXTBYTE();
            c2 = NEXTBYTE();
            c3 = NEXTBYTE();
            c4 = NEXTBYTE();
            
            return (c1 << 24) + (c2 << 16) + (c3 << 8) + c4;
        }
        
        /**
         * reads a DWORD pointer from the file stream.
         * @author Lewis A. Sellers (min)
         * @exception EOFException
         * @returns 32-bit pointer as a long
         */
        long POINTER() throws EOFException {
            long c1;
            long c2;
            long c3;
            long c4;
            c1 = NEXTBYTE();
            c2 = NEXTBYTE();
            c3 = NEXTBYTE();
            c4 = NEXTBYTE();
            
            return (c4 << 24) + (c3 << 16) + (c2 << 8) + c1;
        }
        
        /**
         * SYNCSAFE INTEGER
         * for reading 7-bit bigendian dwords typicalled used in stream syncing w/ mp3
         * @author Lewis A. Sellers (min)
         * @exception EOFException
         * @returns 32-bit syncsafe integer as a long
         */
        long SYNCSAFE_INTEGER() throws EOFException {
            long c1;
            long c2;
            long c3;
            long c4;
            c1 = BYTE();
            c2 = BYTE();
            c3 = BYTE();
            c4 = BYTE();
            
            return ((c1 & 127) << 21) + ((c2 & 127) << 14) + ((c3 & 127) << 7) + c4;
        }
    }