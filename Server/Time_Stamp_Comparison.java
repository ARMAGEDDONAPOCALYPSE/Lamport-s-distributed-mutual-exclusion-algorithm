import java.util.Comparator;

//	Make decision on which regarding to clock value and pid

public class Time_Stamp_Comparison implements Comparator<Time_Stamp> {
	@Override
	public int compare(Time_Stamp o1, Time_Stamp o2) {
		//	Go first with smaller clock value
		if (o1.clock_value.get() < o2.clock_value.get())
			return -1;
		else if (o1.clock_value.get() > o2.clock_value.get())
			return 1;
		else {
			//	Go first with smaller pid
			if (o1.pid < o2.pid)
				return -1;
			else
				return 1;
		}
	}
}
