package org.biobrief.generator.angular;

import org.biobrief.generator.GeneratorConstants.RenderMode;
import org.biobrief.generator.angular.Controls.Control;
import org.biobrief.generator.templates.Form;
import org.biobrief.generator.templates.FormLayout;
import org.biobrief.generator.templates.TableLayout;
import org.biobrief.util.CException;
import org.biobrief.util.StringHelper;

public abstract class AbstractPrimeForm extends CssGrid implements AngularLayout
{
	protected FormLayout form;
	
	public AbstractPrimeForm(FormLayout form)
	{
		super(form);
		this.form=form;
		addClass("mode-"+form.getParams().getMode().name());
	}
	
	public String getName()
	{
		return form.getName();
	}
	
	public Integer getNumCols()
	{
		return form.getNumCols();
	}
	
	public String getTitle()
	{
		return this.form.getTitle();
	}
	
	public RenderMode getRenderMode()
	{
		return check(this.form.getParams().getRenderMode());
	}
	
	public String getSrcfile()
	{
		return check(this.form.getParams().getSrcfile());
	}
	
	public String getOutfile()
	{
		return check(this.form.getParams().getOutfile());
	}
	
	private static <T> T check(T value)
	{
		StringHelper.checkHasContent(value);
		return value;
	}
	
	//@Override
	public boolean isLight()
	{
		return form.getNumCols()==1;
	}
	
	@Override
	protected CssGridCell createCell(TableLayout.Cell cell)
	{
		//System.out.println("creating cell["+cell.getRow()+":"+cell.getCol()+"]: colspan="+cell.getColspan()+" rowspan="+cell.getRowspan());//+value="+cell.getValue()+"
		return new ControlTableCell(cell);
	}

	public static class ControlTableCell extends CssGridCell
	{		
		public ControlTableCell(TableLayout.Cell cell)
		{
			super(cell);
			if (isLabel(cell))
				style.addClass("hirolabel");
			for (TableLayout.Item item : cell.getItems())
			{
				if (item instanceof Form.Control)
					addControl(cell, item);
				else if (item instanceof TableLayout.Text)
					addText(cell, item);
				else throw new CException("unhandled item class: "+item.getClass().getName());
			}
		}
		
		protected void addControl(TableLayout.Cell cell, TableLayout.Item item)
		{
			Form.Control control=(Form.Control)item;
			add(createControl(cell, control));
		}
		
		protected void addText(TableLayout.Cell cell, TableLayout.Item item)
		{
			TableLayout.Text textitem=(TableLayout.Text)item;
			add(textitem.getValue());
		}
		
		private static Control createControl(TableLayout.Cell cell, Form.Control control)
		{	
			//System.out.println("control params: "+StringHelper.toString(control.getParams()));
			if (control.isCalculated())
				return Controls.createFormulaControl(control);
			switch (control.getFieldType())
			{
			case STRING:
			case INTEGER:
			case FLOAT:
				return createTextControl(cell, control);
			case MASTER:
				return Controls.createMasterControl(control);
			case ENUM:
				return Controls.createSelectControl(control);
			case DATE:
				return Controls.createDateControl(control);
			case BOOLEAN:
				return Controls.createCheckboxControl(control);
			default:
				System.err.println("no handler for control type: "+control.getFieldType()+" name="+control.getName());
				return Controls.createUnknownControl(control);
			}
		}
		
		private static Control createTextControl(TableLayout.Cell cell, Form.Control control)
		{
			if (control.getParams().getRows()>1)
				return Controls.createTextAreaControl(control);
			return Controls.createTextControl(control);
		}
		
		// consider non-empty fields without HTML as labels
		private static boolean isLabel(TableLayout.Cell cell)
		{
			return cell.getStyle().hasBackgroundColor();
		}
	}
}
