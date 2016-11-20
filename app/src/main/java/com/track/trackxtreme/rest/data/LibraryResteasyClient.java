/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.track.trackxtreme.rest.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpVersion;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import com.track.trackxtreme.data.track.Track;
import com.track.trackxtreme.rest.LibraryApplication;
import com.track.trackxtreme.rest.LibraryClient;

import android.content.Context;

/**
 * 
 * @author thomas.diesler@jboss.com
 * @since 23-Aug-2011
 */
public class LibraryResteasyClient implements LibraryClient {

	private final Context context;
	private String lastRequestURI;
	private LibraryClient client;
	static {
		ResteasyProviderFactory.setRegisterBuiltinByDefault(false);
	}

	public LibraryResteasyClient(Context context) {
		this.context = context;
		// RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
	}

	@Override
	public List<Track> getTracks() {
		System.out.println("LibraryResteasyClient.getTracks()");

		List<Track> result = new ArrayList<Track>();
		try {
			result = getLibraryClient().getTracks();
		} catch (RuntimeException ex) {
			throw new RuntimeException(ex.getMessage());
		}
		System.out.println("done");
		return result;
	}

	@Override
	public Track getTrack(String isbn) {
		Track result = null;
		try {
			result = getLibraryClient().getTrack(isbn);
		} catch (RuntimeException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	@Override
	public Track addTrack(String isbn, String title) {
		Track result = null;
		try {
			result = getLibraryClient().addTrack(isbn, title);
		} catch (RuntimeException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	@Override
	public Track updateTrack(String isbn, String title) {
		Track result = null;
		try {
			result = getLibraryClient().updateTrack(isbn, title);
		} catch (RuntimeException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	@Override
	public Track removeTrack(String isbn) {
		Track result = null;
		try {
			result = getLibraryClient().removeTrack(isbn);
		} catch (RuntimeException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	private LibraryClient getLibraryClient() {
		String requestURI = LibraryApplication.getRequestURI(context);
		if (client == null || !requestURI.equals(lastRequestURI)) {
			BasicHttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
			HttpProtocolParams.setUseExpectContinue(params, false);
			client = ProxyFactory.create(LibraryClient.class, requestURI, new ApacheHttpClient4Executor(params));
			lastRequestURI = requestURI;
		}
		return client;
	}

	@Override
	public List<Track> getTracktest() {
		// TODO Auto-generated method stub
		return null;
	}

}