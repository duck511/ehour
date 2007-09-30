/**
 * Created on Jun 21, 2007
 * Created by Thies Edeling
 * Copyright (C) 2005, 2006 te-con, All Rights Reserved.
 *
 * This Software is copyright TE-CON 2007. This Software is not open source by definition. The source of the Software is available for educational purposes.
 * TE-CON holds all the ownership rights on the Software.
 * TE-CON freely grants the right to use the Software. Any reproduction or modification of this Software, whether for commercial use or open source,
 * is subject to obtaining the prior express authorization of TE-CON.
 * 
 * thies@te-con.nl
 * TE-CON
 * Legmeerstraat 4-2h, 1058ND, AMSTERDAM, The Netherlands
 *
 */

package net.rrm.ehour.ui.panel.timesheet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.rrm.ehour.config.EhourConfig;
import net.rrm.ehour.customer.domain.Customer;
import net.rrm.ehour.timesheet.domain.TimesheetComment;
import net.rrm.ehour.timesheet.domain.TimesheetCommentId;
import net.rrm.ehour.timesheet.domain.TimesheetEntry;
import net.rrm.ehour.timesheet.dto.WeekOverview;
import net.rrm.ehour.timesheet.service.TimesheetService;
import net.rrm.ehour.ui.ajax.AjaxAwareContainer;
import net.rrm.ehour.ui.ajax.LoadingSpinnerDecorator;
import net.rrm.ehour.ui.ajax.OnClickDecorator;
import net.rrm.ehour.ui.border.GreyBlueRoundedBorder;
import net.rrm.ehour.ui.border.GreyRoundedBorder;
import net.rrm.ehour.ui.component.JavaScriptConfirmation;
import net.rrm.ehour.ui.component.KeepAliveTextArea;
import net.rrm.ehour.ui.model.DateModel;
import net.rrm.ehour.ui.model.FloatModel;
import net.rrm.ehour.ui.panel.timesheet.dto.GrandTotal;
import net.rrm.ehour.ui.panel.timesheet.dto.Timesheet;
import net.rrm.ehour.ui.panel.timesheet.dto.TimesheetRow;
import net.rrm.ehour.ui.panel.timesheet.util.TimesheetAssembler;
import net.rrm.ehour.ui.session.EhourWebSession;
import net.rrm.ehour.ui.util.CommonUIStaticData;
import net.rrm.ehour.user.domain.CustomerFoldPreference;
import net.rrm.ehour.user.domain.User;
import net.rrm.ehour.user.service.UserService;
import net.rrm.ehour.util.DateUtil;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.markup.html.resources.JavaScriptReference;
import org.apache.wicket.markup.html.resources.StyleSheetReference;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * The main panel - timesheet form
 * TODO add feedback for invalid values
 * TODO add 'data saved' feedback
 * TODO add week navigation
 * TODO change store & next week to store
 * TODO add whole week to project
 * TODO fold image itself should change and be clickable
 **/

public class TimesheetPanel extends Panel implements Serializable
{
	private static final long serialVersionUID = 7704288648724599187L;

	@SpringBean
	private TimesheetService	timesheetService;
	@SpringBean
	private UserService			userService;
	
	private	EhourConfig			config;
	public static String[]		dayTotalIds = new String[]{"sundayTotal", "mondayTotal", "tuesdayTotal", 
													"wednesdayTotal", "thursdayTotal", "fridayTotal", "saturdayTotal"};
	
	/**
	 * Construct timesheetPanel for entering hours
	 * @param id
	 * @param user
	 * @param forWeek
	 */
	public TimesheetPanel(String id, User user, Calendar forWeek)
	{
		super(id);
		
		EhourWebSession 	session = (EhourWebSession)getSession();
		SimpleDateFormat	dateFormatter;
		GrandTotal			grandTotals = new GrandTotal();
		
		config = session.getEhourConfig();
		dateFormatter = new SimpleDateFormat("w, MMMM yyyy", config.getLocale());

		// dom id's
		this.setOutputMarkupId(true);
		
		// grey & blue frame border
		GreyRoundedBorder greyBorder = new GreyRoundedBorder("timesheetFrame", "Week " + dateFormatter.format(forWeek.getTime()));
		add(greyBorder);

		// the timesheet we're working on
		final Timesheet timesheet = getTimesheet(user,  forWeek);

		// add form
		Form timesheetForm = new Form("timesheetForm");
		timesheetForm.setOutputMarkupId(true);
		greyBorder.add(timesheetForm);
		
		GreyBlueRoundedBorder blueBorder = new GreyBlueRoundedBorder("blueFrame");
		timesheetForm.add(blueBorder);

		// setup form
		grandTotals = buildForm(timesheetForm, blueBorder, timesheet);
		
		// add last row with grand totals
		addGrandTotals(blueBorder, grandTotals);
		
		// add label dates
		addDateLabels(blueBorder, timesheet);

		// add comments section
		MarkupContainer commentsFrame = createCommentsInput(timesheetForm, timesheet);

		// attach onsubmit ajax events
		setSubmitActions(timesheetForm, commentsFrame, timesheet);

//		// TODO replace with dojo widget
//		FeedbackPanel feedback = new FeedbackPanel("feedback");
//		feedback.setOutputMarkupId(true);
//		add(feedback);
		
		// add CSS & JS
		add(new StyleSheetReference("timesheetStyle", new CompressedResourceReference(TimesheetPanel.class, "style/timesheetForm.css")));
		add(new JavaScriptReference("timesheetJS", new CompressedResourceReference(TimesheetPanel.class, "js/timesheet.js")));
	}

	/**
	 * Create comments input
	 * @param parent
	 * @param timesheet
	 */
	private MarkupContainer createCommentsInput(WebMarkupContainer parent, Timesheet timesheet)
	{
		GreyBlueRoundedBorder blueBorder = new GreyBlueRoundedBorder("commentsFrame");
		
		if (timesheet.getComment() == null)
		{
			timesheet.setComment(new TimesheetComment());
		}
		
		TextArea	textArea = new KeepAliveTextArea("commentsArea", new PropertyModel(timesheet, "comment.comment"));
		
		blueBorder.add(textArea);
		parent.add(blueBorder);
		
		return blueBorder;
	}
	
	/**
	 * Add grand totals to form
	 * @param parent
	 * @param grandTotals
	 */
	private void addGrandTotals(WebMarkupContainer parent, GrandTotal grandTotals)
	{
		Label		total;
		
		for (int i = 0; i < dayTotalIds.length; i++)
		{
			total = new Label(dayTotalIds[i], new FloatModel(new PropertyModel(grandTotals, "getValues[" + i + "]"), config));
			total.setOutputMarkupId(true);
			parent.add(total);
		}

		total = new Label("grandTotal", new FloatModel(new PropertyModel(grandTotals, "grandTotal"), config));
		total.setOutputMarkupId(true);
		parent.add(total);
	}
	
	/**
	 * Set submit actions for form
	 * @param form
	 * @param timesheet
	 */
	private void setSubmitActions(Form form, MarkupContainer parent, final Timesheet timesheet)
	{
		// default submit
		parent.add(new AjaxButton("submitButton", form)
		{
			@Override
            protected void onSubmit(AjaxRequestTarget target, Form form)
			{
                persistTimesheetEntries(timesheet);
                moveToNextWeek(timesheet.getWeekStart(), target);
            }

			@Override
			protected IAjaxCallDecorator getAjaxCallDecorator()
			{
				return new LoadingSpinnerDecorator();
			}			
			
			@Override
			protected void onError(final AjaxRequestTarget target, Form form)
			{
                form.visitFormComponents(new FormHighlighter(target));
            }
        });
		
		// reset, should fetch the original contents
		AjaxButton resetButton = new AjaxButton("resetButton", form)
		{
			@Override
            protected void onSubmit(AjaxRequestTarget target, Form form)
			{
				// basically fake a week click
				((AjaxAwareContainer)getPage()).ajaxRequestReceived(target, CommonUIStaticData.AJAX_CALENDARPANEL_WEEK_CLICK);
            }			
		};

		resetButton.add(new JavaScriptConfirmation("onclick", new ResourceModel("timesheet.confirmReset")));
		
		resetButton.setDefaultFormProcessing(false);
		parent.add(resetButton);
	}
	
	/**
	 * Add date labels (sun/mon etc)
	 */
	private void addDateLabels(WebMarkupContainer parent, Timesheet timesheet)
	{
		Label	label;
		
		label = new Label("sundayLabel", new DateModel(timesheet.getDateSequence()[0], config, DateModel.DATESTYLE_TIMESHEET_DAYLONG));
		label.setEscapeModelStrings(false);
		parent.add(label);

		label = new Label("mondayLabel", new DateModel(timesheet.getDateSequence()[1], config, DateModel.DATESTYLE_TIMESHEET_DAYLONG));
		label.setEscapeModelStrings(false);
		parent.add(label);

		label = new Label("tuesdayLabel", new DateModel(timesheet.getDateSequence()[2], config, DateModel.DATESTYLE_TIMESHEET_DAYLONG));
		label.setEscapeModelStrings(false);
		parent.add(label);

		label = new Label("wednesdayLabel", new DateModel(timesheet.getDateSequence()[3], config, DateModel.DATESTYLE_TIMESHEET_DAYLONG));
		label.setEscapeModelStrings(false);
		parent.add(label);

		Label labelA = new Label("thursdayLabel", new DateModel(timesheet.getDateSequence()[4], config, DateModel.DATESTYLE_TIMESHEET_DAYLONG));
		labelA.setEscapeModelStrings(false);
		parent.add(labelA);
		
		label = new Label("fridayLabel", new DateModel(timesheet.getDateSequence()[5], config, DateModel.DATESTYLE_TIMESHEET_DAYLONG));
		label.setEscapeModelStrings(false);
		parent.add(label);

		label = new Label("saturdayLabel", new DateModel(timesheet.getDateSequence()[6], config, DateModel.DATESTYLE_TIMESHEET_DAYLONG));
		label.setEscapeModelStrings(false);
		parent.add(label);
	}
	
	/**
	 * Move to next week after succesfull form submit
	 * @param target
	 */
	private void moveToNextWeek(Date onScreenDate, AjaxRequestTarget target)
	{
		EhourWebSession session = (EhourWebSession)getSession();
		Calendar	cal = DateUtil.getCalendar(config);
		int			event;

		cal.setTime(onScreenDate);
		cal.add(Calendar.WEEK_OF_YEAR, 1);

		// should update calendar as well
		event = CommonUIStaticData.AJAX_CALENDARPANEL_MONTH_CHANGE;
		session.setNavCalendar(cal);
		
		((AjaxAwareContainer)getPage()).ajaxRequestReceived(target, event);
	}
	
	/**
	 * Persist timesheet entries
	 * @param timesheet
	 */
	private void persistTimesheetEntries(Timesheet timesheet)
	{
		List<TimesheetEntry>	timesheetEntries = new ArrayList<TimesheetEntry>();
		
		Collection<List<TimesheetRow>> rows = timesheet.getCustomers().values();
		
		for (List<TimesheetRow> list : rows)
		{
			for (TimesheetRow timesheetRow : list)
			{
				timesheetEntries.addAll(timesheetRow.getTimesheetEntries());
			}
		}
		
		// check comment id
		if (timesheet.getComment().getCommentId() == null)
		{
			TimesheetCommentId id = new TimesheetCommentId();
			id.setUserId(timesheet.getUser().getUserId());
			id.setCommentDate(timesheet.getWeekStart());
			
			timesheet.getComment().setCommentId(id);
		}
		
		timesheetService.persistTimesheet(timesheetEntries, timesheet.getComment());
	}
	
	/**
	 * Build form
	 * @param parent
	 * @param timesheet
	 */
	private GrandTotal buildForm(final Form form, WebMarkupContainer parent, final Timesheet timesheet)
	{
		final GrandTotal	grandTotals = new GrandTotal();
		
		ListView customers = new ListView("customers", new ArrayList<Customer>(timesheet.getCustomers().keySet()))
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem item)
			{
				final Customer	customer = (Customer)item.getModelObject();

				// check for any preference
				CustomerFoldPreference foldPreference = timesheet.getFoldPreferences().get(customer);
				
				if (foldPreference == null)
				{
					foldPreference = new CustomerFoldPreference(timesheet.getUser(), customer, false);
				}
				
				item.add(getCustomerLabel(customer, foldPreference));
//				item.add(new Label("customerDesc", customer.getDescription()));

				boolean hidden = (foldPreference != null && foldPreference.isFolded());
				
				item.add(new TimesheetRowList("rows", timesheet.getCustomers().get(customer), hidden, grandTotals, form));
			}
		};
		customers.setReuseItems(true);
		
		parent.add(customers);
		
		return grandTotals;
	}

	/**
	 * Get customer label
	 * @param customer
	 * @return
	 */
	private Label getCustomerLabel(final Customer customer, final CustomerFoldPreference foldPreference)
	{
		Label	label;
		
		label = new Label("customer", customer.getName());

		label.add(new AjaxEventBehavior("onclick")
		{
			@Override
			protected void onEvent(AjaxRequestTarget target)
			{
				foldPreference.toggleFolded();
				
				userService.persistCustomerFoldPreference(foldPreference);
			}

			@Override
			protected IAjaxCallDecorator getAjaxCallDecorator()
			{
				return new OnClickDecorator("toggleProjectRow", customer.getCustomerId().toString());
			}					
		});		
		
		return label;
	}
	
	/**
	 * Get timesheet for week
	 * @param user
	 * @param forWeek
	 */
	
	private Timesheet getTimesheet(User user, Calendar forWeek)
	{
		WeekOverview	weekOverview;
		Timesheet		timesheet;
		
		weekOverview = timesheetService.getWeekOverview(user, forWeek);
		
		TimesheetAssembler assembler = new TimesheetAssembler(config);
		timesheet = assembler.createTimesheetForm(weekOverview);
		
		return timesheet;
	}	
}
