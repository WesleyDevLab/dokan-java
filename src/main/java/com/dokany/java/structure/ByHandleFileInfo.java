package com.dokany.java.structure;

import java.util.Date;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.dokany.java.Utils;
import com.dokany.java.constants.FileAttribute;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinNT.LARGE_INTEGER;

/**
 *
 * Contains information that the {@link com.dokany.java.DokanyOperations.GetFileInformation} function retrieves.
 *
 * The identifier that is stored in the nFileIndexHigh and nFileIndexLow members is called the file ID. Support for file IDs is file system-specific. File IDs are not guaranteed to
 * be unique over time, because file systems are free to reuse them. In some cases, the file ID for a file can change over time.
 *
 * In the FAT file system, the file ID is generated from the first cluster of the containing directory and the byte offset within the directory of the entry for the file. Some
 * defragmentation products change this byte offset. (Windows in-box defragmentation does not.) Thus, a FAT file ID can change over time.Renaming a file in the FAT file system can
 * also change the file ID, but only if the new file name is longer than the old one.
 *
 * In the NTFS file system, a file keeps the same file ID until it is deleted. You can replace one file with another file without changing the file ID by using the ReplaceFile
 * function. However, the file ID of the replacement file, not the replaced file, is retained as the file ID of the resulting file.
 *
 * Not all file systems can record creation and last access time, and not all file systems record them in the same manner. For example, on a Windows FAT file system, create time
 * has a resolution of 10 milliseconds, write time has a resolution of 2 seconds, and access time has a resolution of 1 day (the access date). On the NTFS file system, access time
 * has a resolution of 1 hour.
 *
 *
 */
public class ByHandleFileInfo extends Structure implements Structure.ByReference {

	// private final static Logger LOGGER = LoggerFactory.getLogger(ByHandleFileInfo.class);

	// Used to store actual values (instead of high/low) which can be retrieved using getter method
	@NotNull
	String filePath = "";
	long fileIndex;
	long fileSize;

	/**
	 * The high-order DWORD value of the file size, in bytes. This value is zero unless the file size is greater than MAXDWORD. The size of the file is equal to (nFileSizeHigh*
	 * (MAXDWORD+1)) + nFileSizeLow.
	 */
	public int nFileIndexHigh;

	/**
	 * The low-order DWORD value of the file size, in bytes.
	 */
	public int nFileIndexLow;

	/**
	 * The file attributes of a file. For possible values and their descriptions, see File Attribute Constants. The FILE_ATTRIBUTE_SPARSE_FILE attribute on the file is set if any
	 * of the streams of the file have ever been sparse.
	 */
	public int dwFileAttributes;

	/**
	 * A FILETIME structure that specifies when a file or directory was created. If the underlying file system does not support creation time, this member is zero.
	 */
	@NotNull
	public FILETIME ftCreationTime;

	/**
	 * A FILETIME structure. For a file, the structure specifies when the file was last read from, written to, or for executable files, run. For a directory, the structure
	 * specifies when the directory is created. If the underlying file system does not support last access time, this member is zero. On the FAT file system, the specified date for
	 * both files and directories is correct, but the time of day is always set to midnight.
	 */
	@NotNull
	public FILETIME ftLastAccessTime;

	/**
	 * A FILETIME structure. For a file, the structure specifies when the file was last written to, truncated, or overwritten, for example, when WriteFile or SetEndOfFile are used.
	 * The date and time are not updated when file attributes or security descriptors are changed. For a directory, the structure specifies when the directory is created. If the
	 * underlying file system does not support last write time, this member is zero.
	 */
	@NotNull
	public FILETIME ftLastWriteTime;

	/**
	 * The high-order DWORD value of the file size, in bytes. This value is zero unless the file size is greater than MAXDWORD. The size of the file is equal to (nFileSizeHigh *
	 * (MAXDWORD+1)) + nFileSizeLow.
	 */
	public int nFileSizeHigh;

	/**
	 * The low-order DWORD value of the file size, in bytes.
	 */
	public int nFileSizeLow;

	public int dwVolumeSerialNumber;
	public int dwNumberOfLinks = 1;

	public ByHandleFileInfo() {
		setTimes(0, 0, 0);
	}

	public void copyTo(final ByHandleFileInfo infoToReceive) {
		if (Utils.isNull(infoToReceive)) {
			throw new IllegalStateException("infoToReceive cannot be null");
		}

		infoToReceive.filePath = filePath;

		infoToReceive.setSize(fileSize, nFileSizeHigh, nFileSizeLow);

		infoToReceive.setIndex(fileIndex, nFileIndexHigh, nFileIndexLow);

		infoToReceive.dwFileAttributes = dwFileAttributes;

		infoToReceive.setTimes(ftCreationTime, ftLastAccessTime, ftLastWriteTime);

		infoToReceive.dwNumberOfLinks = dwNumberOfLinks;
		infoToReceive.dwVolumeSerialNumber = dwVolumeSerialNumber;
	}

	public void setAttributes(final FileAttribute attributes) {
		int toSet = FileAttribute.NORMAL.val;
		if (Utils.isNotNull(this)) {
			toSet = attributes.val;
		}
		dwFileAttributes = toSet;
	}

	public void setTimes(final long creationTime, final long lastAccessTime, final long lastWriteTime) {
		final Date now = new Date();

		ftCreationTime = creationTime == 0 ? new FILETIME(now) : new FILETIME(new Date(creationTime));
		ftLastAccessTime = lastAccessTime == 0 ? new FILETIME(now) : new FILETIME(new Date(lastAccessTime));
		ftLastWriteTime = lastWriteTime == 0 ? new FILETIME(now) : new FILETIME(new Date(lastWriteTime));
	}

	void setTimes(final FILETIME creationTime, final FILETIME lastAccessTime, final FILETIME lastWriteTime) {
		ftCreationTime = creationTime;
		ftLastAccessTime = lastAccessTime;
		ftLastWriteTime = lastWriteTime;
	}

	/**
	 * Also sets lastAccessTime to same time.
	 *
	 * @param lastWriteTimeToSet
	 * @return
	 */
	public void setLastWriteTime(final long lastWriteTime) {
		ftLastWriteTime = lastWriteTime == 0 ? new FILETIME(new Date()) : new FILETIME(new Date(lastWriteTime));
		ftLastAccessTime = ftLastWriteTime;
	}

	/**
	 * Also sets lastAccessTime to same time.
	 *
	 * @param lastWriteTimeToSet
	 * @return
	 */
	public void setCreationTime(final long creationTime) {
		ftCreationTime = creationTime == 0 ? new FILETIME(new Date()) : new FILETIME(new Date(creationTime));
	}

	public void setSize(final long sizeToSet) {
		setSize(sizeToSet, 0, 0);
	}

	final void setSize(final long size, final int sizeHigh, final int sizeLow) {
		fileSize = size;

		final LARGE_INTEGER largeInt = getLargeInt(size, sizeHigh, sizeLow);

		nFileSizeHigh = ((size != 0) && (sizeHigh == 0)) ? largeInt.getHigh().intValue() : (int) size;
		nFileSizeLow = ((size != 0) && (sizeLow == 0)) ? largeInt.getLow().intValue() : (int) size;

	}

	public final long getSize() {
		return fileSize;
	}

	public void setIndex(final long indexToSet) {
		setIndex(indexToSet, 0, 0);
	}

	final void setIndex(final long index, final int indexHigh, final int indexLow) {
		fileIndex = index;

		final LARGE_INTEGER largeInt = getLargeInt(index, indexHigh, indexLow);

		nFileIndexHigh = ((index != 0) && (indexHigh == 0)) ? largeInt.getHigh().intValue() : (int) index;
		nFileIndexLow = ((index != 0) && (indexLow == 0)) ? largeInt.getLow().intValue() : (int) index;
	}

	private static final LARGE_INTEGER getLargeInt(final long val, final int high, final int low) {
		LARGE_INTEGER largeInt = null;
		if ((val != 0) && ((high == 0) || (low == 0))) {
			largeInt = new LARGE_INTEGER(val);
		}
		return largeInt;
	}

	@Override
	public String toString() {
		return "FileInfo{" +
		        "attributes=" + dwFileAttributes +
		        ", creationTime=" + ftCreationTime +
		        ", lastAccessTime=" + ftLastAccessTime +
		        ", lastWriteTime=" + ftLastWriteTime +
		        ", fileSize=" + fileSize +
		        ", fileName='" + filePath + '\'' +
		        ", fileIndex=" + fileIndex +
		        ", numberOfLinks=" + dwNumberOfLinks +
		        ", volumeSerialNumber=" + dwVolumeSerialNumber +
		        '}';
	}

	@Override
	public List<String> getFieldOrder() {
		return createFieldsOrder(
		        "dwFileAttributes",
		        "ftCreationTime", "ftLastAccessTime", "ftLastWriteTime",
		        "dwVolumeSerialNumber",
		        "nFileSizeHigh", "nFileSizeLow",
		        "dwNumberOfLinks",
		        "nFileIndexHigh", "nFileIndexLow");
	}
}