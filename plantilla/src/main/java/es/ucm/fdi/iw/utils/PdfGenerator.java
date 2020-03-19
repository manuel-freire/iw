package es.ucm.fdi.iw.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Table.Cell;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import es.ucm.fdi.iw.control.UserController;
import es.ucm.fdi.iw.model.StClass;
import es.ucm.fdi.iw.model.User;

public class PdfGenerator {
	
	private static final Logger log = LogManager.getLogger(PdfGenerator.class);

	public static void generateQrClassFile(List<User> users, StClass stClass) throws DocumentException, MalformedURLException, IOException {
//		Chunk chunkUser, chunkName;
		String id, name, img;
		User u;
		Image qrCode;
		int numPages;
		int cellCount = 0;
		
		String qrFile = Constants.QR_FILE + "." + Constants.PDF;
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(qrFile));
		Font font = FontFactory.getFont(FontFactory.COURIER, Constants.QR_FONT_SIZE, BaseColor.BLACK);
		PdfPTable table;
		
		document.open();		
//		for (User u : users) {
//			id = Long.toString(u.getId());
//			name = u.getFirstName() + " " + u.getLastName();
//			img = Constants.QR_IMG + u.getUsername() + "." + Constants.PNG;
//			
//			chunkName = new Chunk(name, font);
//			chunkUser = new Chunk(img, font); 
//			document.add(chunkName);
//	        document.add(Chunk.NEWLINE);
//			document.add(chunkUser);
//			
//			QrGenerator.generateQrCode(id, u.getUsername());
//			qrCode = Image.getInstance(img);
//			document.add(qrCode);
//		}
		
		if (users.size() % Constants.NUM_ROWS == 0) {
			numPages = users.size() / Constants.NUM_ROWS;
		} else {
			numPages = (users.size() / Constants.NUM_ROWS) + 1;
		}

		for (int i=0; i < numPages; i++) {
			for (int j=0; j < Math.min(Constants.NUM_ROWS, users.size()-cellCount); j++) {
				cellCount = Constants.NUM_ROWS*i+j;
				u = users.get(cellCount);
				id = Long.toString(u.getId());
				name = u.getFirstName() + ",\n" + u.getLastName();
				img = Constants.QR_IMG + u.getUsername() + "." + Constants.PNG;
				
				log.info("Creando QR para el usuario {}", cellCount);
				table = new PdfPTable(Constants.NUM_COLS);
				table.getDefaultCell().setFixedHeight(Constants.CELL_H);
				table.setWidths(new float[] {Constants.NAME_W, Constants.USER_W, Constants.QR_W});
				table.addCell(new Phrase(name, font));
				table.addCell(new Phrase(img, font));
				QrGenerator.generateQrCode(id, u.getUsername());
				qrCode = Image.getInstance(img);
				table.addCell(qrCode);
				table.completeRow();
				document.add(table);
			}
			
			document.newPage();
		}
		
		document.close();
	}
}
