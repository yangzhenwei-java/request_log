package com.github.log.filter.wrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


public class WrapperedResponse extends HttpServletResponseWrapper{

	
	private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	
	public WrapperedResponse(HttpServletResponse response) {
		super(response);
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return new ServletOutputStream(){

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setWriteListener(WriteListener writeListener) {
				
			}

			@Override
			public void write(int b) throws IOException {
				outputStream.write(b);
			}
			
		};
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		
		return new PrintWriter(new OutputStreamWriter(outputStream));
	}
	
	public byte[] getByteArray(){
		return outputStream.toByteArray();
	}
	
	public void setByteArray(byte[] byteArray) throws IOException{
		outputStream.reset();
		outputStream.write(byteArray);
		outputStream.flush();
	}

	
}
