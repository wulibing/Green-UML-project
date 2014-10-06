package nonDefaultPackage;

public enum DayOfWeek {

	Monday{
		public DayOfWeek previousDay() { return Tuesday; }
	}, Tuesday, Wednesday, Thursday, Friday;
	
//	Tuesday {
//		public DayOfWeek nextDay() { return Wednesday; }
//	},
//	Wednesday {
//		public DayOfWeek nextDay() { return Thursday; }
//	},
//	Thursday {
//		public DayOfWeek nextDay() { return Friday; }
//	},
//	Friday {
//		public DayOfWeek nextDay() { return Friday; }
//	};
	
//	public abstract DayOfWeek nextDay();
	public DayOfWeek previousDay() { return Friday; }
	
	public static void main(String... args) {
		System.out.println(Monday);
//		System.out.println(Monday.nextDay());
		System.out.println(Monday.previousDay());
	}
}
