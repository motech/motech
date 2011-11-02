package org.motechproject.mobileforms.api.web;

import com.jcraft.jzlib.ZOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class FormUploadServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        InputStream input = request.getInputStream();
        OutputStream output = response.getOutputStream();
        ZOutputStream zOutput = null;
        DataInputStream dataInput = null;
        DataOutputStream dataOutput = null;
        response.setContentType("application/octet-stream");

    }
}
