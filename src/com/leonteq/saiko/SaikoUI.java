
package com.leonteq.saiko;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("serial")
@Theme("saiko")
public class SaikoUI extends UI
{

	public static final String		NAME			= "login";

	private TextField				user			= new TextField();

	private PasswordField			password		= new PasswordField();

	private Button					loginButton		= new Button();

	TabSheet						tabsheet;


	VerticalLayout					loginPage		= new VerticalLayout();
	TabSheet						mainAppPage		= new TabSheet();

	String							tx				= "";


	public static String			SRC1			= "/home/hadoop/hermes/I1.pdf";

	Window							win				= new Window();

	public static VerticalLayout	termsheetTab	= null;


	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = SaikoUI.class)
	public static class Servlet extends VaadinServlet
	{
	}




	//@Override
	protected void init2(VaadinRequest request)
	{

		//
		// Create a new instance of the navigator. The navigator will attach
		// itself automatically to this view.
		//
		new Navigator(this, this);

		//
		// The initial log view where the user can login to the application
		//
		getNavigator().addView(SimpleLoginView.NAME, SimpleLoginView.class);//

		//
		// Add the main view of the application
		//
		getNavigator().addView(SimpleLoginMainView.NAME, SimpleLoginMainView.class);

		//
		// We use a view change handler to ensure the user is always redirected
		// to the login view if the user is not logged in.
		//
		getNavigator().addViewChangeListener(new ViewChangeListener()
		{

			@Override
			public boolean beforeViewChange(ViewChangeEvent event)
			{

				// Check if a user has logged in
				boolean isLoggedIn = getSession().getAttribute("user") != null;
				boolean isLoginView = event.getNewView() instanceof SimpleLoginView;

				if (!isLoggedIn && !isLoginView)
				{
					// Redirect to login view always if a user has not yet
					// logged in
					getNavigator().navigateTo(SimpleLoginView.NAME);
					return false;

				}
				else if (isLoggedIn && isLoginView)
				{
					// If someone tries to access to login view while logged in,
					// then cancel
					return false;
				}

				return true;
			}




			@Override
			public void afterViewChange(ViewChangeEvent event)
			{

			}
		});
	}




	@Override
	protected void init(VaadinRequest request)
	{


		setContent(loginPage);


		tx = request.getParameter("tx");
		if (tx == null)
		{
			tx = "Unknown";
		}



		getLoginPage(loginPage, tx);




	}




	VerticalLayout getLoginPage(VerticalLayout viewLayout, String tx)
	{

		viewLayout.setMargin(true);
		setContent(viewLayout);




		Button button = new Button(tx);


		button.addClickListener(new Button.ClickListener()
		{

			public void buttonClick(ClickEvent event)
			{


			}
		});

		// Create the user input field
		user = new TextField("User:");
		user.setValue("test@test.com");
		user.setWidth("300px");
		user.setRequired(true);
		user.setInputPrompt("Your username (eg. joe@email.com)");
		user.addValidator(new EmailValidator("Username must be an email address"));
		user.setInvalidAllowed(false);

		// Create the password input field
		password = new PasswordField("Password:");
		password.setValue("passw0rd");
		password.setWidth("300px");
		password.addValidator(new PasswordValidator());
		password.setRequired(true);
		password.setValue("");
		password.setNullRepresentation("");

		// Create login button
		loginButton = new Button("Login");
		loginButton.addClickListener(new Button.ClickListener()
		{

			public void buttonClick(ClickEvent event)
			{

				setContent(getMainAppPage());

			}
		});


		// Add both to a panel
		VerticalLayout fields = new VerticalLayout(user, password, loginButton);
		fields.setCaption("Please login to access the application. (test@test.com/passw0rd)");
		fields.setSpacing(true);
		fields.setMargin(new MarginInfo(true, true, true, false));
		fields.setSizeUndefined();

		// The view root layout
		viewLayout.addComponent(fields);
		viewLayout.setSizeFull();
		viewLayout.setComponentAlignment(fields, Alignment.TOP_CENTER);
		viewLayout.setStyleName(Reindeer.LAYOUT_BLUE);


		viewLayout.addStyleName("aria");




		return viewLayout;


	}




	void viewTermsheet(String filename)
	{
		Window window = new Window();
		window.setResizable(true);
		window.setCaption("Termsheet");
		window.setWidth("1000");
		window.setHeight("800");
		window.center();
		StreamSource s = new StreamSource()
		{

			@Override
			public InputStream getStream()
			{
				try
				{
					File f = new File(filename);
					FileInputStream fis = new FileInputStream(f);
					return fis;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return null;
				}
			}
		};

		StreamResource r = new StreamResource(s, filename);
		

		
		r.setMIMEType("application/pdf");
	    r.setCacheTime(0);
//	    r.getStream().setParameter("Content-Disposition", "attachment; filename=" + filename);
	    
	    BrowserFrame frame = new BrowserFrame(null, r);
		frame.setSizeFull();
		
		
		Embedded e = new Embedded("", r);
		e.setSizeFull();
		e.setType(Embedded.TYPE_OBJECT);
		//e.setMIMEType("application/pdf");

		//e.setSource(r);
		window.setContent(frame);
		UI.getCurrent().addWindow(window);
	}



	class PdfStreamSource implements StreamSource 
	{
		byte[] ba;


		public PdfStreamSource(String filename)
		{

			ba = getFileBytes(filename);
			System.out.println("Pdf ctr ********** getFileBytes ok");
		}




		@Override
		public InputStream getStream()
		{
			System.out.println("Pdf ********** getStream ok");
			return new ByteArrayInputStream(ba);
		}
	}


	BrowserFrame ffs;

	TabSheet getMainAppPage()
	{


		File pdf = new File("/home/hadoop/hermes/ts1.pdf");
		FileResource fr = new FileResource(pdf);

		byte[] ba = null;
		ba = getFileBytes("/home/hadoop/hermes/ts1.pdf");
		final byte[] bytearray = ba;

		StreamResource streamResource = new StreamResource(new StreamResource.StreamSource()
		{

			@Override
			public InputStream getStream()
			{
				ByteArrayInputStream bas = new ByteArrayInputStream(bytearray);
				System.out.println("********** CALLING getstream");
				return bas;
			}
		}, "ts1.pdf");



		StreamSource pdfss = new PdfStreamSource("/home/hadoop/hermes/ts1.pdf");
		StreamResource resource = new StreamResource(pdfss, "ts1.pdf");

		resource.setMIMEType("application/pdf");
		resource.setCacheTime(0);


		//yaya
		StreamSource mysource = new StreamSource()
		{

			public InputStream getStream1()
			{
				System.out.println("Pdf ctr ********** StreamSource mysource ok");
				return new InputStream () {
					int sz = 20;
					public int read() throws IOException {
						if (sz-- > 0) {
							return 'v';
						}
						return -1;
					}
				};
			}
			@Override
			public InputStream getStream()
			{
				System.out.println("Pdf ctr ********** StreamSource mysource ok (pdf bytes");
				return new ByteArrayInputStream(getFileBytes("/home/hadoop/hermes/ts1.pdf"));
				
			}
		};
		
		resource = new StreamResource(mysource, "ts1.pdf" );
		ffs = new BrowserFrame(null, resource);
		ffs.setSizeFull();
				
//		Embedded e = new Embedded("ts1.pdf", openResourceFromStream("/home/hadoop/hermes/ts1.pdf"));
		Embedded e = new Embedded("ts1.pdf", resource);
		


		//e.setSource(resource);

		//e.setSizeFull();
		//e.setSource(streamResource);
		//e.setMimeType("application/pdf");
		//e.setType(Embedded.TYPE_OBJECT);

		Window window = new Window();
		window.setResizable(true);
		window.setCaption("Termsheet autocall");
		window.setWidth("1000");
		window.setHeight("800");
		window.center();

		window.setContent(ffs);
		UI.getCurrent().addWindow(window);


		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


		tabsheet = new TabSheet();

		// Create the first tab
		VerticalLayout tab1 = new VerticalLayout();
		tab1.addComponent(new Label("Distributer Compliance"));




		tabsheet.addTab(tab1, "Distributer Compliance", null);

		VerticalLayout tab2 = new VerticalLayout();
		tab2.addComponent(new Label("Client Compliance"));
		tabsheet.addTab(tab2, "Client Compliance", null);

		termsheetTab = new VerticalLayout();


		pdfgeneration(termsheetTab);

		//		termsheetTab.addComponent(new Label("Termsheet"));

		tabsheet.addTab(termsheetTab, "Termsheet", null);

		VerticalLayout tab4 = new VerticalLayout();
		tab4.addComponent(new Label("Transmission Link = " + tx));
		tabsheet.addTab(tab4, "Audit", null);

		return tabsheet;

	}




	public static byte[] getFileBytes(String fileName) 
	{
		ByteArrayOutputStream ous = null;
		InputStream ios = null;
		try
		{
			byte[] buffer = new byte[4096];
			ous = new ByteArrayOutputStream();
			ios = new FileInputStream(new java.io.File(fileName));
			int read = 0;
			while ((read = ios.read(buffer)) != -1)
				ous.write(buffer, 0, read);
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (ous != null)
					ous.close();
			}
			catch (IOException e)
			{
				// swallow, since not that important
			}
			try
			{
				if (ios != null)
					ios.close();
			}
			catch (IOException e)
			{
				// swallow, since not that important
			}
		}
		return ous.toByteArray();
	}




	public Resource openResourceFromStream(String filename)
	{
		byte [] ba =  null;
		ba = getFileBytes(filename);
		
		final byte [] fba = ba;
		
		StreamSource streamSource = new StreamSource()
		{

			private static final long serialVersionUID = 1L;




			@Override
			public InputStream getStream()
			{
				System.out.println("openResourceFromStream !*****");
				return new ByteArrayInputStream(fba);
			}
		};

		StreamResource resultis =  new StreamResource(streamSource, filename);
		resultis.setMIMEType("application/pdf");
		resultis.setCacheTime(0);
//		resultis.getStream().setParameter("Content-Disposition", "attachment; filename=ts1.pdf");		
		
		return resultis;
		

	}




	VerticalLayout vspace(int h)
	{
		VerticalLayout layout = new VerticalLayout();
		Label l = new Label("<p>&nbsp</p>", ContentMode.HTML);
		l.setHeight(String.format("%dpx", h));
		layout.addComponent(l);
		return layout;

	}




	HorizontalLayout hspace(int h)
	{
		HorizontalLayout layout = new HorizontalLayout();
		Label l = new Label("<p>&nbsp</p>", ContentMode.HTML);
		l.setWidth(String.format("%dpx", h));
		layout.addComponent(l);
		return layout;

	}


	TextField tf = new TextField("TS Location");




	public void pdfgeneration(VerticalLayout layout2)
	{

		Button print = new Button("Open pdf");

		tf.setValue("/home/hadoop/hermes/ts1.pdf");
		tf.setWidth("400px");

		print.addClickListener(new ClickListener()
		{

			private static final long serialVersionUID = 1269425538593656695L;




			@Override
			public void buttonClick(ClickEvent event)
			{
				viewTermsheet(tf.getValue());
				int i = 1;
			}
		});


		HorizontalLayout hl = new HorizontalLayout();
		hl.addComponent(hspace(5));

		VerticalLayout v2 = new VerticalLayout();
		v2.addComponent(vspace(10));
		v2.addComponent(print);

		hl.addComponent(v2);
		hl.addComponent(hspace(35));
		hl.addComponent(tf);

		layout2.addComponent(vspace(5));
		layout2.addComponent(hl);




	}




	private static final class PasswordValidator extends AbstractValidator<String>
	{

		public PasswordValidator()
		{
			super("The password provided is not valid");
		}




		@Override
		protected boolean isValidValue(String value)
		{
			//
			// Password must be at least 8 characters long and contain at least
			// one number
			//
			if (value != null && (value.length() < 8 || !value.matches(".*\\d.*")))
			{
				return false;
			}
			return true;
		}




		@Override
		public Class<String> getType()
		{
			return String.class;
		}
	}




	public static void main(String[] args)
	{
		SaikoUI ui = new SaikoUI();



	}
}