package org.biobrief.util;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *	 notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *	 notice, this list of conditions and the following disclaimer in the
 *	 documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *	 contributors may be used to endorse or promote products derived
 *	 from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Example to watch a directory (or tree) for changes to files.
 * https://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java
 * https://stackoverflow.com/questions/7801662/java-nio-file-watchevent-gives-me-only-relative-path-how-can-i-get-the-absolute4https://docs.oracle.com/javase/tutorial/essential/io/notification.html
 */
@SuppressWarnings("rawtypes")
public class DirectoryWatcher
{
	private final WatchService watcher;
	private final Map<WatchKey,Path> keys;
	private final boolean recursive;
	private final DirectoryWatchEventHandler handler;
	private boolean trace = false;

	public DirectoryWatcher(String rootFolder, boolean recursive, DirectoryWatchEventHandler handler)
	{
		this(Paths.get(rootFolder), recursive, handler);
	}
	
	/**
	 * Creates a WatchService and registers the given directory
	 */
	public DirectoryWatcher(Path dir, boolean recursive, DirectoryWatchEventHandler handler)
	{
		try
		{
			this.watcher = FileSystems.getDefault().newWatchService();
			this.keys = new HashMap<WatchKey,Path>();
			this.recursive = recursive;
			this.handler=handler;
	
			if (recursive)
			{
				System.out.format("Scanning %s ...\n", dir);
				registerAll(dir);
				//System.out.println("Done.");
			}
			else
			{
				register(dir);
			}
	
			// enable trace after initial registration
			this.trace = true;
		}
		catch (IOException e)
		{
			throw new CException(e);
		}
	}

	/**
	 * Process all events for keys queued to the watcher
	 */
	public void watch()
	{
		for (;;)
		{
			// wait for key to be signaled
			WatchKey key;
			try
			{
				key = watcher.take();
			}
			catch (InterruptedException x)
			{
				return;
			}

			Path dir = keys.get(key);
			if (dir == null)
			{
				System.err.println("WatchKey not recognized!!");
				continue;
			}

			for (WatchEvent<?> event: key.pollEvents())
			{
				WatchEvent.Kind kind = event.kind();				
				// TBD - provide example of how OVERFLOW event is handled
				if (kind == OVERFLOW)
				{
					System.err.println("DirectoryWatcher: OVERFLOW event occurred");
					continue;
				}

				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);

				// print out event
				System.out.format("%s: %s\n", event.kind().name(), child);
				handler.onEvent(new DirectoryWatchEvent(ev, child));

				// if directory is created, and watching recursively, then
				// register it and its sub-directories
				if (recursive && (kind == ENTRY_CREATE))
				{
					try
					{
						if (Files.isDirectory(child, NOFOLLOW_LINKS))
							registerAll(child);
					}
					catch (IOException x)
					{
						// ignore to keep sample readbale
					}
				}
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid)
			{
				keys.remove(key);
				// all directories are inaccessible
				if (keys.isEmpty())
					break;
			}
		}
	}
	
	/**
	 * Register the given directory with the WatchService
	 */
	private void register(Path dir) throws IOException
	{
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		if (trace)
		{
			Path prev = keys.get(key);
			if (prev == null)
			{
				System.out.format("register: %s\n", dir);
			}
			else
			{
				if (!dir.equals(prev))
				{
					System.out.format("update: %s -> %s\n", prev, dir);
				}
			}
		}
		keys.put(key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void registerAll(final Path start) throws IOException
	{
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
				throws IOException
			{
				register(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event)
	{
		return (WatchEvent<T>)event;
	}
	
	static void usage()
	{
		System.err.println("usage: java WatchDir [-r] dir");
		System.exit(-1);
	}
	
	public interface DirectoryWatchEventHandler
	{
		void onEvent(DirectoryWatchEvent event);
	}

	public static class DefaultDirectoryWatchEventHandler implements DirectoryWatchEventHandler
	{
		public void onEvent(DirectoryWatchEvent event)
		{
			System.out.println("DefaultWatchEventHandler.onEvent: "+StringHelper.toString(event));
		}
	}
	
	public static class DirectoryWatchEvent
	{
		protected WatchEvent<Path> event;
		protected Path path;
		
		public DirectoryWatchEvent(WatchEvent<Path> event, Path path)
		{
			this.event=event;
			this.path=path;
		}
		
		//https://stackoverflow.com/questions/2723838/determine-file-creation-date-in-java
		public Date getCreatedDate()
		{
			return FileHelper.getCreatedDate(path.toFile());
		}
		
		//https://www.mkyong.com/java/how-to-get-the-file-last-modified-date-in-java/
		public Date getLastModifiedDate()
		{
			return new Date(path.toFile().lastModified());
		}
		
		public boolean isCreate()
		{
			return event.kind()==StandardWatchEventKinds.ENTRY_CREATE;
		}
		
		public boolean isModify()
		{
			return event.kind()==StandardWatchEventKinds.ENTRY_MODIFY;
		}
		
		public boolean isDelete()
		{
			return event.kind()==StandardWatchEventKinds.ENTRY_DELETE;
		}
		
		public boolean isFile()
		{
			return event.context().toFile().isFile();
		}
		
		public boolean isDirectory()
		{
			return event.context().toFile().isDirectory();
		}
		
		public String getFilename()
		{
			return path.toString();
		}

		public String getParentDir()
		{
			return path.toFile().getParentFile().getName();
		}
		
		@Override
		public String toString()
		{
			return "kind="+event.kind()+" context="+event.context()+" path="+path+" created="+getCreatedDate()+" modified="+getLastModifiedDate();
		}
		
		public WatchEvent<Path> getEvent(){return this.event;}
		public Path getPath(){return this.path;}
	}
	
	public static void main(String[] args) throws IOException
	{
		// parse arguments
		if (args.length == 0 || args.length > 2)
			usage();
		boolean recursive = false;
		int dirArg = 0;
		if (args[0].equals("-r"))
		{
			if (args.length < 2)
				usage();
			recursive = true;
			dirArg++;
		}

		// register directory and process its events
		Path dir = Paths.get(args[dirArg]);
		new DirectoryWatcher(dir, recursive, new DefaultDirectoryWatchEventHandler()).watch();
	}
}