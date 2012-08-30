/*
See make.bat for compilation instructions.
 */

//package com.intrafoundation;

import java.io.File;

//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.FilenameFilter;
//import java.io.IOException;
import java.util.Date;

import com.allaire.cfx.CustomTag;
import com.allaire.cfx.Query;
import com.allaire.cfx.Request;
import com.allaire.cfx.Response;

//import item.class;

/**
 * 
 * 
 * 
 * CFX_AudioInfo Works with all Java versions of ColdFusion MX and up.
 * 
 * This Java language ColdFusion extension tag (that is, a CFX) for Adobe
 * ColdFusion MX and up, CFX_AudioInfo, will scan either a single file or an
 * entire folder, looking for audio files of mp3, wav, au and midi format. From
 * these files it will gather up all manner of information such as song title,
 * author, compression type, bitrate, song length, etc. Handles ID3v1, ID3v1.1
 * and ID3v2 4.0.
 * 
 * 
 * The source code to this program is included as open source. It has been
 * tested under Windows only at the moment. If you have compiled and used it on
 * other platforms please feel free to drop a note.
 * 
 * This obsoletes the original 32-bit C++ version.
 * 
 * Edited in Eclipse.
 * 
 * 
 * @version 3.0a
 * @author Lewis A. Sellers (min)
 * @see #READ
 * @see #ITEM
 * @see #EOFException
 **/
public class CFX_AudioInfo implements CustomTag {
	READ read = new READ();
	private ITEM item = new ITEM();
	Query query = null;

	/**
	 * This tag output the following variables: AudioInfoDescription
	 * AudioInfoVersion AudioInfoFolder AudioInfoFile AudioInfoFilter
	 * AudioInfoError
	 **/

	/**
	 * 
	 * 
	 * 
	 * Main processing method for custom tag
	 **/
	public void processRequest(Request request, Response response)
			throws Exception {
		try {
			//
			response.setVariable(
					"AudioInfoDescription",
					"OPEN SOURCE. FREEWARE. Java. Returns information on WAV, AU, MIDI and MP3. Written by Lewis A. Sellers, webmaster@intrafoundation.com. Copyright (c) 1999, 2000, 2003, 2012. Intrafoundation Software, http://www.intrafoundation.com.");
			response.setVariable("AudioInfoVersion", "3.0a, August 30th 2012");

			//
			response.setVariable("AudioInfoFolder", "");
			response.setVariable("AudioInfoFile", "");
			response.setVariable("AudioInfoFilter", "");

			//
			String szfolder = request.getAttribute("FOLDER");
			String szfile = request.getAttribute("FILE");
			String szfilter = request.getAttribute("FILTER");

			if (szfolder == null)
				szfolder = "";

			if (szfile == null)
				szfile = "";

			if (szfilter == null)
				szfilter = "";

			response.setVariable("AudioInfoFile", szfile);
			response.setVariable("AudioInfoFolder", szfolder);
			response.setVariable("AudioInfoFilter", szfilter);

			//
			query = response.addQuery("AudioInfo", columns);

			//
			if ((szfolder.length() == 0) && (szfile.length() == 0))
				;
			else if (szfolder.indexOf("..") != -1)
				log.log_error(log.iGRAMMAR,
						"Relative folder locations not permitted in FOLDER.");
			else if ((szfolder.indexOf("*") != -1)
					|| (szfolder.indexOf("?") != -1))
				log.log_error(log.iGRAMMAR,
						"Wildcard characters (* or ?) not permitted in FOLDER.");
			else if ((szfile.indexOf("*") != -1) || (szfile.indexOf("?") != -1))
				log.log_error(log.iGRAMMAR,
						"Wildcard characters (* or ?) not permitted in FILE.");
			else if ((szfilter.indexOf("*") != -1)
					|| (szfilter.indexOf("?") != -1))
				log.log_error(log.iGRAMMAR,
						"Wildcard characters (* or ?) not permitted in FILTER.");
			else if ((szfilter.length() > 0) && (szfolder.length() == 0))
				log.log_error(log.iGRAMMAR, "FILTER must be used with FOLDER.");
			else if (szfile.length() > 0) { // file
				int i = szfile.lastIndexOf('\\');
				if (i > 0) {
					String sfolder = szfile.substring(0, i) + "\\";
					String sfilter = szfile.substring(i + 1);
					String ss = sfolder + sfilter;
					audioInfo(ss);
				} else
					audioInfo(szfile);
			} else if (szfilter.length() > 0) { // filter
				try {
					File f = new File(szfolder);
					try {
						FilenameFilter sff = new SingleFilenameFilter(szfilter);
						File[] subf = f.listFiles(sff);
						for (int i = 0; i < subf.length; i++)
							audioInfo(subf[i].getAbsolutePath());
					} catch (SecurityException e) {
						log.log_error(log.iSECURITY,
								"File permissions do not allow access to this file/folder.");
					}
				} catch (NullPointerException e) {
					log.log_error(log.iFILEDOESNOTEXIST,
							"Invalid handle while getting directory ("
									+ szfolder + ").");
				}
			} else if (szfolder.length() > 0) { // folder
				try {
					File f = new File(szfolder);
					try {
						File[] subf = f.listFiles();
						for (int i = 0; i < subf.length; i++)
							audioInfo(subf[i].getAbsolutePath());
					} catch (SecurityException e) {
						log.log_error(log.iSECURITY,
								"File permissions do not allow access to this file/folder.");
					}
				} catch (NullPointerException e) {
					log.log_error(log.iFILEDOESNOTEXIST,
							"Invalid handle while getting directory ("
									+ szfolder + ").");
				}
			}

			//
			response.setVariable("AudioInfoError", log.get_error());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Names of all fields returned from a query.
	 **/
	final String[] columns = { "File", "Path", "Type", "LastModified", "Size",
			"Error", "Extra", "Encoding", "Frequency", "BitRate", "BitRateMin",
			"BitRateMax", "Channels", "isCopyright", "isOriginal", "Seconds",
			"Title", "Composer", "Artist", "Album", "Year", "Genre", "Country",
			"Language", "Track", "Tracks", "Lyrics", "Comments", "Source",
			"URL", "InfoType", "Picture", "Copyright" };
	final int iFile = 1;
	final int iPath = 2;
	final int iType = 3;
	final int iLastModified = 4;
	final int iSize = 5;
	final int iError = 6;
	final int iExtra = 7;
	final int iEncoding = 8;
	final int iFrequency = 9;
	final int iBitRate = 10;
	final int iBitRateMin = 11;
	final int iBitRateMax = 12;
	final int iChannels = 13;
	final int iisCopyright = 14;
	final int iisOriginal = 15;
	final int iSeconds = 16;
	final int iTitle = 17;
	final int iComposer = 18;
	final int iArtist = 19;
	final int iAlbum = 20;
	final int iYear = 21;
	final int iGenre = 22;
	final int iCountry = 23;
	final int iLanguage = 24;
	final int iTrack = 25;
	final int iTracks = 26;
	final int iLyrics = 27;
	final int iComments = 28;
	final int iSource = 29;
	final int iURL = 30;
	final int iInfoType = 31;
	final int iPicture = 32;
	final int iCopyright = 33;

	/**
	 * Class that contains query field data temporarily.
	 */
	/*
	 * class ITEM { String File; String Path; String Type; String Error; long
	 * Size; //i64 String LastModified; String Extra; String Encoding; int
	 * Frequency; String BitRate; int BitRateMin; int BitRateMax; int Channels;
	 * boolean isCopyright; boolean isOriginal; int Seconds; String Title;
	 * String Artist; String Composer; String Album; String Year; String Genre;
	 * String Country; String Language; int Track; int Tracks; String Lyrics;
	 * String Comments; String Source; String URL; String InfoType; String
	 * Picture; String Copyright; }
	 */

	/**
     **/
	private String to2(int n) {
		String tmp = Integer.toString(n + 100);
		return tmp.substring(1, 3);
	}

	/**
     **/
	private String to4(int n) {
		String tmp = Integer.toString(n + 10000);
		return tmp.substring(1, 5);
	}

	/**
	 * 
	 * 
	 * 
	 * This converts a standard "long" second count from 1970 into the SQL ODBC
	 * format timestamp: "{ts '%04d-%02d-%02d %02d:%02d:%02d'}",
	 * 
	 * @returns: ODBC String of ts format
	 **/
	@SuppressWarnings("deprecation")
	private String longToSQLTS(long t) {
		Date d = new Date(t);
		String tmp = "{ts '";
		tmp += (to4(d.getYear() + 1900) + "-");
		tmp += (to2(d.getMonth()) + "-");
		tmp += (to2(d.getDay()) + " ");
		tmp += (to2(d.getHours()) + ":");
		tmp += (to2(d.getMinutes()) + ":");
		tmp += (to2(d.getSeconds()) + "}");
		return tmp;
	}

	/**
     *
     *
     *
     *
     **/
	private void audioInfo(String szfile) {
		try {
			File f = new File(szfile);
			if (f.isFile()) {
				// file
				item.Error = "";
				item.File = f.getName();
				item.Path = f.getAbsolutePath();
				item.LastModified = longToSQLTS(f.lastModified());
				item.Size = f.length();
				item.Type = "";

				item.Extra = "";
				item.Encoding = "";
				item.Frequency = 0;
				item.BitRate = "";
				item.BitRateMin = 0;
				item.BitRateMax = 0;
				item.Channels = 0;
				item.isCopyright = false;
				item.isOriginal = false;
				item.Seconds = 0;

				item.Title = "";
				item.Artist = "";
				item.Composer = "";
				item.Album = "";
				item.Year = "";
				item.Genre = "";
				item.Country = "";
				item.Language = "";

				item.Track = 0;
				item.Tracks = 0;

				item.Lyrics = "";
				item.Comments = "";
				item.Source = "";
				item.URL = "";
				item.InfoType = "";

				item.Picture = "";
				item.Copyright = "";

				//
				if (!read.open(szfile))
					log.log_error(log.iFILEDOESNOTEXIST,
							"File could not be opened for reading (" + szfile
									+ ").");
				else {
					//
					boolean valid = true;
					boolean knowntype = true;

					String ext;
					int p = szfile.lastIndexOf('.');
					if (p == -1)
						ext = "";
					else {
						ext = szfile.substring(p + 1, szfile.length());
						ext = ext.toLowerCase();
					}

					if (ext.equalsIgnoreCase("wav"))
						valid = wav();
					else if (ext.equalsIgnoreCase("au"))
						valid = au();
					else if (ext.equalsIgnoreCase("mp3"))
						valid = mp3();
					else if (ext.equalsIgnoreCase("mid")
							|| ext.equalsIgnoreCase("midi"))
						valid = midi();
					else if (ext.equalsIgnoreCase("mod"))
						valid = mod();
					else if (ext.equalsIgnoreCase("aiff"))
						valid = aiff();
					else if (ext.equalsIgnoreCase("ra"))
						valid = ra();
					else if (ext.equalsIgnoreCase("asf"))
						valid = asf();
					else if (ext.equalsIgnoreCase("wma"))
						valid = asf();
					else if (ext.equalsIgnoreCase("ogg"))
						valid = ogg();
					else if (ext.equalsIgnoreCase("mp4")
							|| ext.equalsIgnoreCase("m4a")
							|| ext.equalsIgnoreCase("m4p"))
						valid = mp4();
					else if (ext.equalsIgnoreCase("aac"))
						valid = aac();
					else
						knowntype = false; // "Unknown file extension."

					//
					read.close();

					//
					if (knowntype) {
						if (!valid)
							log_itemerror(log.iCORRUPT,
									"File format corrupt or wrong file extension.");

						//
						int iRow = query.addRow();
						query.setData(iRow, iFile, item.File);
						query.setData(iRow, iPath, item.Path);
						query.setData(iRow, iError, item.Error);
						query.setData(iRow, iType, item.Type);
						query.setData(iRow, iLastModified, item.LastModified);

						query.setData(iRow, iSize, String.valueOf(item.Size));

						query.setData(iRow, iExtra, item.Extra);
						query.setData(iRow, iEncoding, item.Encoding);
						query.setData(iRow, iFrequency,
								String.valueOf(item.Frequency));
						query.setData(iRow, iBitRate, item.BitRate);

						query.setData(iRow, iBitRateMin,
								String.valueOf(item.BitRateMin));
						query.setData(iRow, iBitRateMax,
								String.valueOf(item.BitRateMax));
						query.setData(iRow, iChannels,
								String.valueOf(item.Channels));
						query.setData(iRow, iisCopyright,
								item.isCopyright ? "true" : "false");
						query.setData(iRow, iisOriginal,
								item.isOriginal ? "true" : "false");

						query.setData(iRow, iSeconds,
								String.valueOf(item.Seconds));

						query.setData(iRow, iTitle, item.Title);
						query.setData(iRow, iArtist, item.Artist);
						query.setData(iRow, iAlbum, item.Album);
						query.setData(iRow, iYear, item.Year);
						query.setData(iRow, iGenre, item.Genre);
						query.setData(iRow, iCountry, item.Country);
						query.setData(iRow, iLanguage, item.Language);

						query.setData(iRow, iTrack, String.valueOf(item.Track));
						query.setData(iRow, iTracks,
								String.valueOf(item.Tracks));

						query.setData(iRow, iLyrics, item.Lyrics);
						query.setData(iRow, iComments, item.Comments);
						query.setData(iRow, iSource, item.Source);
						query.setData(iRow, iURL, item.URL);
						query.setData(iRow, iInfoType, item.InfoType);

						query.setData(iRow, iPicture, item.Picture);
						query.setData(iRow, iCopyright, item.Copyright);
					}
				}
			}
		} catch (NullPointerException e) {
			log.log_error(log.iFILEDOESNOTEXIST, "File does not exist ("
					+ szfile + ").");
		}
	}

	/**
	 * 
	 * 
	 * 
	 * MIDI
	 **/
	@SuppressWarnings("unused")
	private boolean midi() {
		item.Type = "MIDI";

		try {
			//
			String MThd = read.STRING(4);

			long headersize = read.DWORD();
			int fileformat = read.WORD();
			int numberoftracks = read.WORD();
			int deltatimeticksperquarternote = read.WORD();

			//
			switch (fileformat) {
			case 0:
				item.Encoding = "Single-track";
				break;
			case 1:
				item.Encoding = "Multiple tracks, Synchronous";
				break;
			case 2:
				item.Encoding = "Multiple tracks, Asynchronous";
				break;
			default:
				item.Encoding = "";
				break;
			}
			item.Channels = numberoftracks;

			return true;
		} catch (EOFException e) {
			log_itemerror(log.iCORRUPT, "Premature EOF.");
			return false;
		}
	}

	/**
	 * 
	 * 
	 * 
	 * SUN Microsystems AU Format
	 * 
	 * The following is supposedly the sound format used by Sun and Next
	 * machines:
	 * 
	 * [ From: mrose@dbc.mtview.ca.us (Marshall Rose) ]
	 * 
	 * Audio data is encoded in three parts: a header, containing fields that
	 * describe the audio encoding format; a variable-length information field,
	 * in which, for instance, ASCII annotation may be stored; and, the actual
	 * encoded audio. The header and data fields are written using big-endian
	 * ordering.
	 * 
	 * The header part consists of six 32-bit quantities, in this order:
	 * 
	 * longword field description -------- ----- ----------- 0 magic number the
	 * value 0x2e736e64 (ASCII ".snd")
	 * 
	 * 1 data offset the offset, in octets, to the data part. The minimum valid
	 * number is 24 (decimal).
	 * 
	 * 2 data size the size in octets, of the data part. If unknown, the value
	 * 0xffffffff should be used.
	 * 
	 * 3 encoding the data encoding format:
	 * 
	 * value format 1 8-bit ISDN u-law 2 8-bit linear PCM [REF-PCM] 3 16-bit
	 * linear PCM 4 24-bit linear PCM 5 32-bit linear PCM 6 32-bit IEEE floating
	 * point 7 64-bit IEEE floating point 23 8-bit ISDN u-law compressed using
	 * the CCITT G.721 ADPCM voice data encoding scheme.
	 * 
	 * 4 sample rate the number of samples/second (e.g., 8000)
	 * 
	 * 5 channels the number of interleaved channels (e.g., 1)
	 * 
	 * 
	 * The information part, consists of 0 or more octets, and starts 24 octets
	 * after the beginning of the header part. The length of the information
	 * part is calculated by subtracting 24 (decimal) from the data offset field
	 * in the header part. -- Bill Janssen janssen@parc.xerox.com (415) 812-4763
	 * Xerox Palo Alto Research Center FAX: (415) 812-4777 3333 Coyote Hill
	 * Road, Palo Alto, California 94304
	 **/
	@SuppressWarnings("unused")
	private boolean au() {
		item.Type = "AU";

		try {
			// header
			long magicnumber = read.DWORD_BIGENDIAN();
			long dataoffset = read.DWORD_BIGENDIAN();
			long datasize = read.DWORD_BIGENDIAN();
			long encoding = read.DWORD_BIGENDIAN();
			long samplerate = read.DWORD_BIGENDIAN();
			long channels = read.DWORD_BIGENDIAN();

			if (magicnumber == 0x2e736e64) // ASCII ".snd"
			{
				switch ((int) encoding) {
				case 1:
					item.Encoding = "8-bit ISDN u-law";
					break;
				case 2:
					item.Encoding = "8-bit linear PCM [REF-PCM]";
					break;
				case 3:
					item.Encoding = "16-bit linear PCM";
					break;
				case 4:
					item.Encoding = "24-bit linear PCM";
					break;
				case 5:
					item.Encoding = "32-bit linear PCM";
					break;
				case 6:
					item.Encoding = "32-bit IEEE floating point";
					break;
				case 7:
					item.Encoding = "64-bit IEEE floating point";
					break;
				case 23:
					item.Encoding = "8-bit ISDN u-law compressed using the CCITT G.721 ADPCM voice data encoding scheme";
					break;
				}

				item.Channels = (int) channels;
				item.Frequency = (int) samplerate;
			} else {
				log_itemerror(log.iCORRUPT, "MagicNumber wrong.");
				return false;
			}

			//
			int len = ((int) dataoffset - 24);
			if (len > 0)
				item.Comments = read.STRING(len);

			return true;
		} catch (EOFException e) {
			log_itemerror(log.iCORRUPT, "Premature EOF.");
			return false;
		}
	}

	/**
	 * 
	 * 
	 * 
	 * Microsoft WAV format (oh no, not another "simple" microsoft format.
	 * Compare it to Sun's AU.)
	 **/
	@SuppressWarnings("unused")
	private boolean wav() {
		item.Type = "WAV";

		try {
			//
			class RIFF_HDR {
				String id; // identifier string = "RIFF" [4]
				long len; // remaining length after this header
			}

			RIFF_HDR riff_hdr = new RIFF_HDR();
			String wave_id; // WAVE file identifier = "WAVE" [4]

			class CHUNK_HDR { // CHUNK 8-byte header
				String id; // identifier, e.g. "fmt " or "data" [4]
				long len; // remaining chunk length after header
			}

			CHUNK_HDR chunk_hdr = new CHUNK_HDR();

			//
			class FMT {
				int wFormatTag; // Format category
				int wChannels; // Number of channels
				long dwSamplesPerSec; // Sampling rate
				long dwAvgBytesPerSec; // For buffer estimation
				int wBlockAlign; // Data block size
			}

			FMT fmt = new FMT();

			//
			riff_hdr.id = read.STRING(4);
			riff_hdr.len = read.DWORD();

			wave_id = read.STRING(4);

			//
			long bytes = riff_hdr.len;

			//
			while ((bytes > 8) && !read.isEOF()) {
				riff_hdr.id = read.STRING(4);
				riff_hdr.len = read.DWORD();
				bytes -= 8;

				if (riff_hdr.id.equals("fmt ")) {
					// fmt
					fmt.wFormatTag = read.WORD();
					fmt.wChannels = read.WORD();
					fmt.dwSamplesPerSec = read.DWORD();
					fmt.dwAvgBytesPerSec = read.DWORD();
					fmt.wBlockAlign = read.WORD();

					if (fmt.wFormatTag != 1) {
						int clen = read.BYTE();
						read.skip(clen);
					} else
						read.skip((int) riff_hdr.len - 14);
				} else if (riff_hdr.id.equals("INFO")) {
					int len = (int) riff_hdr.len;
					if (len > 0)
						item.Comments = read.STRING(len);
				} else if (riff_hdr.id.equals("data"))
					read.skip((int) riff_hdr.len);
				else
					read.skip((int) riff_hdr.len);
				bytes -= riff_hdr.len;
			}
			;

			//
			item.Channels = fmt.wChannels;
			item.Frequency = (int) fmt.dwSamplesPerSec;
			int br = (int) (fmt.dwAvgBytesPerSec * (8.0 / 1000.0));
			item.BitRate = String.valueOf(br);
			item.BitRateMin = br;
			item.BitRateMax = br;

			switch (fmt.wFormatTag) {
			case 0x001:
				item.Encoding = "Microsoft Pulse Code Modulation (PCM)";
				break;
			case 0x101:
				item.Encoding = "IBM mu-law format";
				break;
			case 0x102:
				item.Encoding = "IBM a-law format";
				break;
			case 0x103:
				item.Encoding = "IBM AVC Adaptive Differential PCM format";
				break;
			}

			return true;
		} catch (EOFException e) {
			log_itemerror(log.iCORRUPT, "Premature EOF.");
			return false;
		}
	}

	/**
	 * 
	 * 
	 * 
	 * Mpeg Layer 3 audio
	 * 
	 * Based on header information from:
	 * http://www.dv.co.yu/mpgscript/mpeghdr.htm
	 * 
	 * -------------------------------------------------- 21-32 11 sync 0xFFF
	 * 19-20 1 sv version 1=mpeg1.0, 0=mpeg2.0 17-18 2 lay 4-lay = layerI, II or
	 * III 16 1 error protection 0=yes, 1=no 11-15 4 bitrate_index see table
	 * below 09-10 2 sampling_freq see table below 08 1 padding 07 1 extension
	 * see table below 05-06 2 mode see table below 03-04 2 mode_ext used with
	 * "joint stereo" mode 03 1 copyright 0=no 1=yes 02 1 original 0=no 1=yes
	 * 00-01 2 emphasis see table below
	 * --------------------------------------------------
	 */
	@SuppressWarnings("unused")
	private boolean mp3() {
		item.Type = "MP3";

		final String[] genres = { "Blues", "Classic Rock", "Country", "Dance",
				"Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal",
				"New Age", "Oldies", "Other", "Pop", "R&B", "Rap", "Reggae",
				"Rock", "Techno", "Industrial", "Alternative", "Ska",
				"Death Metal", "Pranks", "Soundtrack", "Euro-Techno",
				"Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion",
				"Trance", "Classical", "Instrumental", "Acid", "House", "Game",
				"Sound Clip", "Gospel", "Noise", "AlternRock", "Bass", "Soul",
				"Punk", "Space", "Meditative", "Instrumental Pop",
				"Instrumental Rock", "Ethnic", "Gothic", "Darkwave",
				"Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance",
				"Dream", "Southern Rock", "Comedy", "Cult", "Gangsta",
				"Top 40", "Christian Rap", "Pop/Funk", "Jungle",
				"Native American", "Cabaret", "New Wave", "Psychadelic",
				"Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk",
				"Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll",
				"Hard Rock", "Folk", "Folk-Rock", "National Folk", "Swing",
				"Fast Fusion", "Bebob", "Latin", "Revival", "Celtic",
				"Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock",
				"Psychedelic Rock", "Symphonic Rock", "Slow Rock", "Big Band",
				"Chorus", "Easy Listening", "Acoustic", "Humour", "Speech",
				"Chanson", "Opera", "Chamber Music", "Sonata", "Symphony",
				"Booty Brass", "Primus", "Porn Groove", "Satire", "Slow Jam",
				"Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad",
				"Rhytmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo",
				"A Capela", "Euro-House", "Dance Hall" };
		// bitrates
		final int[] mpeg1layer1 = { 32, 64, 96, 128, 160, 192, 224, 256, 288,
				320, 352, 384, 416, 448 };
		final int[] mpeg1layer2 = { 32, 48, 56, 64, 80, 96, 112, 128, 160, 192,
				224, 256, 320, 384 };
		final int[] mpeg1layer3 = { 32, 40, 48, 56, 64, 80, 96, 112, 128, 160,
				192, 224, 256, 320 };
		final int[] mpeg2layer1 = { 32, 48, 56, 64, 80, 96, 112, 128, 144, 160,
				176, 192, 224, 256 };
		final int[] mpeg2layer2 = { 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112,
				128, 144, 160 };
		final int[] mpeg2layer3 = { 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112,
				128, 144, 160 };

		try {
			// ////////////////////////////////////////////////////////////////
			// ID3, ID3v1.1, ID3v2
			int hpos = 0;

			// /////////////////
			// ID3v1 and ID3v1.1
			read.setPosition((int) item.Size - 128);
			String szTag = read.STRING(3);
			if (szTag.equals("TAG")) {
				hpos = 0;

				//
				item.Title = read.STRING(30);
				item.Artist = read.STRING(30);
				item.Album = read.STRING(30);
				item.Year = read.STRING(4);
				item.Comments = read.STRING(29);

				// item.Copyright=item.Comments;
				// TRACK id3v1.1 only
				item.Track = read.BYTE();
				if (item.Track > 0)
					item.InfoType = "ID3v1.1 ";
				else
					item.InfoType = "ID3v1 ";

				//
				int genre = read.BYTE();
				if (genre == 0xff)
					item.Genre = "[Undefined]";
				else if (genre <= 126)
					item.Genre = genres[genre];
				else
					item.Genre = "[Unknown=" + String.valueOf(genre) + "]";
			}

			// /////////////////
			// ID3v2
			read.setPosition(0);
			String ident = read.STRING(3);
			if (ident.equals("ID3")) {
				//
				int major = read.BYTE();
				int revision = read.BYTE();
				int flags = read.BYTE();

				item.InfoType = "ID3v2 " + String.valueOf(major) + "."
						+ String.valueOf(revision);

				int hsize = (int) read.SYNCSAFE_INTEGER();
				hpos = hsize + 10;

				//
				String FrameID;
				int FrameSize;
				int Flags;
				int bytes = hsize;
				do {
					FrameID = read.STRING(4);
					FrameSize = (int) read.DWORD_BIGENDIAN();
					Flags = read.WORD();
					if (FrameSize > 0) {
						int textencoding = read.BYTE();
						if (FrameID.equals("TENC")) {
							switch (textencoding) {
							case 0:
								item.Extra += " (ISO-8859-1)";
								break; // $00 ISO-8859-1 [ISO-8859-1].
										// Terminated with $00.
							case 1:
								item.Extra += " (UTF-16)";
								break; // $01 UTF-16 [UTF-16] encoded Unicode
										// [UNICODE] with BOM. All strings in
										// the same frame SHALL have the same
										// byteorder. Terminated with $00 00.
							case 2:
								item.Extra += " (UTF-16BE)";
								break; // $02 UTF-16BE [UTF-16] encoded Unicode
										// [UNICODE] without BOM. Terminated
										// with $00 00.
							case 3:
								item.Extra += " (UTF-8)";
								break; // $03 UTF-8 [UTF-8] encoded Unicode
										// [UNICODE]. Terminated with $00.
							}
						}

						int textsize = FrameSize - 1;

						if (FrameID.equals("TALB"))
							item.Album = read.STRING(textsize);
						else if (FrameID.equals("TDAT"))
							item.Year = read.STRING(textsize);
						else if (FrameID.equals("TIT1"))
							item.Title = read.STRING(textsize);
						else if (FrameID.equals("TIT2"))
							item.Title = read.STRING(textsize);
						else if (FrameID.equals("TIT3"))
							item.Title = read.STRING(textsize);
						else if (FrameID.equals("TLAN"))
							item.Language = read.STRING(textsize);
						else if (FrameID.equals("TPE1"))
							item.Artist = read.STRING(textsize);
						else if (FrameID.equals("TPE2"))
							item.Artist = read.STRING(textsize);
						else if (FrameID.equals("TPE3"))
							item.Artist = read.STRING(textsize);
						else if (FrameID.equals("TYER"))
							item.Year = read.STRING(textsize);
						else if (FrameID.equals("TCON"))
							item.Genre = read.STRING(textsize);
						else if (FrameID.equals("TENC"))
							item.Source = read.STRING(textsize);
						else if (FrameID.equals("TLEN")) {
							String tmp = read.STRING(textsize);
							item.Seconds = Integer.parseInt(tmp);
						} else if (FrameID.equals("WXXX"))
							item.URL = read.STRING(textsize);
						else if (FrameID.equals("COMM")) {
							String tmp = read.STRING(textsize);
							item.Comments = tmp.substring(3);
						} else if (FrameID.equals("TCOM"))
							item.Composer = read.STRING(textsize);
						else if (FrameID.equals("TCOP"))
							item.Copyright = "Copyright (C) "
									+ read.STRING(textsize);
						else if (FrameID.equals("TRCK")) {
							String tmp = read.STRING(textsize);
							int delim = tmp.indexOf("/");
							try {
								if (delim == -1) {
									item.Track = Integer.parseInt(tmp);
									item.Tracks = 0;
								} else {
									item.Track = Integer.parseInt(tmp
											.substring(0, delim));
									item.Tracks = Integer.parseInt(tmp
											.substring(delim + 1));
								}
							} catch (NumberFormatException e) {
								item.Track = 0;
								item.Tracks = 0;
							}
						}
						// 'Unsynchronised lyrics/text transcription', ID:
						// "USLT"
						else if (FrameID.equals("USLT")) {
							long pos = read.getPosition();

							String Language = read.STRING(3);
							String Descriptor = read.STRING();
							int len = textsize
									- (1 + 3 + Descriptor.length() + 1) + 2;
							String Lyrics = read.STRING(len);

							item.Lyrics = Lyrics;

							read.setPosition((int) (pos + textsize));
						} else if (FrameID.equals("APIC")) {
							long pos = read.getPosition();

							int textEncoding = read.BYTE();
							String mimeType = read.STRING();
							int pictureType = read.BYTE();
							String Description = read.STRING();

							//
							if (item.Picture.length() > 0)
								item.Picture += ",";

							//
							switch (pictureType) {
							case 0:
								item.Picture = "Other";
								break;
							case 1:
								item.Picture = "32x32 pixels 'file icon' (PNG only)";
								break;
							case 2:
								item.Picture = "Other file icon";
								break;
							case 3:
								item.Picture = "Cover (front)";
								break;
							case 4:
								item.Picture = "Cover (back)";
								break;
							case 5:
								item.Picture = "Leaflet page";
								break;
							case 6:
								item.Picture = "Media (e.g. label side of CD)";
								break;
							case 7:
								item.Picture = "Lead artist/lead performer/soloist";
								break;
							case 8:
								item.Picture = "Artist/performer";
								break;
							case 9:
								item.Picture = "Conductor";
								break;
							case 10:
								item.Picture = "Band/Orchestra";
								break;
							case 11:
								item.Picture = "Composer";
								break;
							case 12:
								item.Picture = "Lyricist/text writer";
								break;
							case 13:
								item.Picture = "Recording Location";
								break;
							case 14:
								item.Picture = "During recording";
								break;
							case 15:
								item.Picture = "During performance";
								break;
							case 16:
								item.Picture = "Movie/video screen capture";
								break;
							case 17:
								item.Picture = "A bright coloured fish";
								break;
							case 18:
								item.Picture = "Band/artist logotype";
								break;
							case 19:
								item.Picture = "Publisher/Studio logotype";
								break;
							}

							//
							read.setPosition((int) (pos + textsize));
						} else
							read.skip(textsize);
					}

					bytes -= (10 + FrameSize);
				} while ((bytes > 0) && (FrameSize > 0));
			}

			// ////////////////////////////////////////////////////////////////
			//
			read.setPosition(hpos);

			//
			boolean vbr = false;
			long header = 0L;
			do
				header = read.DWORD_BIGENDIAN();
			while (header == 0);
			int framesync = (int) (header >> 21) & 0x7ff;
			if (framesync != 0x7ff) {
				log_itemerror(log.iCORRUPT, "File format invalid (framesync "
						+ String.valueOf(framesync) + ").");
				return false;
			}

			//
			int MPEGAudioVersionID = (int) (header >> 19) & 3;
			switch (MPEGAudioVersionID) {
			case 0:
				item.Encoding += "MPEG 2.5 ";
				break;
			case 1:
				item.Encoding += "reserved-version ";
				break;
			case 2:
				item.Encoding += "MPEG 2 ";
				break; // (ISO/IEC 13818-3)
			case 3:
				item.Encoding += "MPEG 1 ";
				break; // (ISO/IEC 11172-3)
			}

			int LayerDescription = (int) (header >> 17) & 3;
			switch (LayerDescription) {
			case 0:
				item.Encoding += "reserved-layer ";
				break;
			case 1:
				item.Encoding += "Layer III ";
				break;
			case 2:
				item.Encoding += "Layer II ";
				break;
			case 3:
				item.Encoding += "Layer I ";
				break;
			}

			int ProtectionBit = (int) (header >> 16) & 1;
			switch (ProtectionBit) {
			case 0:
				item.Extra += "(Error Protection) ";
				break;
			case 1:
				item.Extra += " ";
				break;
			}

			//
			int FrequencyIndex = (int) (header >> 10) & 3;
			int SampleRate = 0;
			switch (MPEGAudioVersionID) {
			case 3:
				switch (FrequencyIndex) {
				case 0:
					SampleRate = 44100;
					break;
				case 1:
					SampleRate = 48000;
					break;
				case 3:
					SampleRate = 32000;
					break;
				}
				break;
			case 2:
				switch (FrequencyIndex) {
				case 0:
					SampleRate = 22050;
					break;
				case 1:
					SampleRate = 24000;
					break;
				case 3:
					SampleRate = 16000;
					break;
				}
				break;
			case 0:
				switch (FrequencyIndex) {
				case 0:
					SampleRate = 11025;
					break;
				case 1:
					SampleRate = 12000;
					break;
				case 3:
					SampleRate = 8000;
					break;
				}
				break;
			}
			item.Frequency = SampleRate;

			int Padding = (int) (header >> 9) & 1;
			switch (Padding) {
			case 0:
				item.Extra += " ";
				break;
			case 1:
				item.Extra += "(Frame padded with one extra slot) ";
				break;
			}

			int Private = (int) (header >> 8) & 1;
			switch (Private) {
			case 0:
				item.Extra += "(Private) ";
				break;
			case 1:
				item.Extra += " ";
				break;
			}

			int ChannelMode = (int) (header >> 6) & 3;
			switch (ChannelMode) {
			case 0:
				item.Encoding += "Stereo ";
				item.Channels = 2;
				break;
			case 1:
				item.Encoding += "Joint stereo (Stereo) ";
				item.Channels = 2;
				break;
			case 2:
				item.Encoding += "Dual channel (Stereo) ";
				item.Channels = 2;
				break;
			case 3:
				item.Encoding += "Single channel (Mono) ";
				item.Channels = 1;
				break;
			}

			int ModeExtension = 0;
			if (ChannelMode == 1) {
				ModeExtension = (int) (header >> 4) & 3;
				switch (ModeExtension) {
				case 0:
					item.Encoding += "LR_LR";
					break;
				case 1:
					item.Encoding += "LR_I";
					break;
				case 2:
					item.Encoding += "MS_LR";
					break;
				case 3:
					item.Encoding += "MS_I";
					break;
				}
			}

			item.isCopyright = (((header >> 4) & 1) == 1) ? true : false;
			item.isOriginal = (((header >> 3) & 1) == 1) ? true : false;

			int Emphasis = (int) header & 3;
			switch (Emphasis) {
			case 0:
				item.Encoding += " ";
				break;
			case 1:
				item.Encoding += "50/15 ms ";
				break;
			case 2:
				item.Encoding += "reserved-emphasis ";
				break;
			case 3:
				item.Encoding += "CCIT J.17 ";
				break;
			}

			// bitrate [14]
			int BitrateIndex = (int) (header >> 12) & 15;
			int BitRate = 0;
			if (BitrateIndex <= 0) { // ==
				item.BitRate = "variable";
				// vbr=true;
			} else if (BitrateIndex >= 15) // ==

				item.BitRate = "forbidden";
			else {
				switch (MPEGAudioVersionID) {
				case 3:
					switch (LayerDescription) {
					case 3:
						BitRate = mpeg1layer1[BitrateIndex - 1];
						break;
					case 2:
						BitRate = mpeg1layer2[BitrateIndex - 1];
						break;
					case 1:
						BitRate = mpeg1layer3[BitrateIndex - 1];
						break;
					}
					break;
				case 2:
					switch (LayerDescription) {
					case 3:
						BitRate = mpeg2layer1[BitrateIndex - 1];
						break;
					case 2:
						BitRate = mpeg2layer2[BitrateIndex - 1];
						break;
					case 1:
						BitRate = mpeg2layer3[BitrateIndex - 1];
						break;
					}
					break;
				}

				item.BitRate = Integer.toString(BitRate);
				item.BitRateMin = BitRate;
				item.BitRateMax = BitRate;

				int FrameSize;
				try {
					FrameSize = ((144 * BitRate) / SampleRate) + Padding;
				} catch (ArithmeticException e) {
					FrameSize = 0;
				}

				read.skip(FrameSize);
			}

			// //////////////////////////////////////////////////////////////////////
			// special VBR code that runs through entire mp3 looking at min/max
			// rates
			if (vbr) {
				// do nothing right now ...
			}

			// //////////////////////////////////////////////////////////////////////
			return true;
		} catch (EOFException e) {
			log_itemerror(log.iCORRUPT, "Premature EOF.");
			return false;
		}
	}

	/**
	 * MOD (future expansion)
	 */
	private boolean mod() {
		item.Type = "MOD";
		return false;
	}

	/**
	 * AIFF (future expansion)
	 */
	private boolean aiff() {
		item.Type = "AIFF";
		return false;
	}

	/**
	 * ASF, WMA (future expansion)
	 */
	private boolean asf() {
		item.Type = "WMA";
		return false;
	}

	/**
	 * OGG (future expansion)
	 */
	private boolean ogg() {
		item.Type = "OGG";
		return false;
	}

	/**
	 * mp4, m4a, m4p (future expansion)
	 */
	private boolean mp4() {
		item.Type = "MP4";
		return false;
	}

	/**
	 * ra (Real Audio) (future expansion)
	 */
	private boolean ra() {
		item.Type = "RA";
		return false;
	}

	/**
	 * aac (Advanced Audio Codec) (future expansion)
	 */
	private boolean aac() {
		item.Type = "AAC";
		return false;
	}

	/**
	 * 
	 * 
	 * 
	 * Logs specific errors that occur during the decoding of a file. In
	 * general, this is will never report iFILEDOESNOTEXIST or iSECURITY type
	 * errors, but may return any other known error type.
	 * 
	 * The error is returned as part of the query data in the "Error" field.
	 * 
	 * @parm err Numeric error type code such as iCORRUPT, etc.
	 * @parm error Text string giving detailed information on the error.
	 * @return void
	 **/
	public void log_itemerror(int err, String error) {
		item.Error += log.format_error(err, error);
	}
}
