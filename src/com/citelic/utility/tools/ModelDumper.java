package com.citelic.utility.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.alex.store.Index;
import com.alex.store.Store;
import com.citelic.cache.Cache;

public class ModelDumper {

	public static void main(String[] args) throws IOException {
		Cache.STORE = new Store("D:/cache/", true);
		Index index = Cache.STORE.getIndexes()[7];
		for (int archiveId : index.getTable().getValidArchiveIds()) {
			// for (int fileId :
			// index.getTable().getArchives()[archiveId].getValidFileIds()) {
			for (int fileId = 0; fileId < index.getLastArchiveId(); fileId++) {
				byte[] data = index.getFile(fileId);
				System.out.println(fileId);
				if (data == null) {
					System.out.println("failed " + fileId + " in archive "
							+ archiveId);
					continue;
				}
				writeFile(data, "D:/models718/" + fileId + ".dat");
			}
		}

	}

	public static void writeFile(byte[] data, String fileName)
			throws IOException {
		OutputStream out = new FileOutputStream(fileName);
		out.write(data);
		out.close();
	}

}
