package org.biobrief.pipelines.commands;

public class TestCommand extends AbstractCommand
{
	public TestCommand()
	{
		super("run_test.sh --num $num");
	}
	
	public Command num(Integer num)
	{
		return param("num", num);
	}
}
