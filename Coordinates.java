/**
 * This class contains coordinates for description of lines (may be used also for description of an single point) 
 * @author MH
 *
 * @param <A>
 */
public class Coordinates<A> {
		private A xStart;
		private A yStart;
		private A xStop;
		private A yStop;

		public Coordinates(A xStart, A yStart, A xStop, A yStop) {
			super();
			this.xStart = xStart;
			this.yStart = yStart;
			this.xStop = xStop;
			this.yStop = yStop;
		}
		
		public Coordinates() {
			super();
			this.xStart = null;
			this.yStart = null;
			this.xStop = null;
			this.yStop = null;
		}
		
		public boolean equals(Object other) {
			if (other instanceof Coordinates) {
				Coordinates<?> otherCoordinates = (Coordinates<?>) other;
				
				Boolean isStartEq = ((this.xStart == otherCoordinates.xStart
						|| (this.xStart != null && otherCoordinates.xStart != null && this.xStart.equals(otherCoordinates.xStart)))
						&& (this.yStart == otherCoordinates.yStart || (this.yStart != null && otherCoordinates.yStart != null
								&& this.yStart.equals(otherCoordinates.yStart))));
				
				Boolean isStopEq = ((this.xStop == otherCoordinates.xStop
						|| (this.xStop != null && otherCoordinates.xStop != null && this.xStop.equals(otherCoordinates.xStop)))
						&& (this.yStop == otherCoordinates.yStop || (this.yStop != null && otherCoordinates.yStop != null
								&& this.yStop.equals(otherCoordinates.yStop))));
				return isStartEq && isStopEq;
			}

			return false;
		}

		public String toString() {
			return "(" + xStart + " , " + yStart + ")" + " ; (" + xStop + " , " + yStop +")";
		}

		public A getxStart() {
			return this.xStart;
		}
		public A getyStart() {
			return this.yStart;
		}
		public A getxStop() {
			return this.xStop;
		}
		public A getyStop() {
			return this.yStop;
		}
		
		
		public void setStart(A xStart, A yStart){
			this.xStart = xStart;
			this.yStart = yStart;
		}
		
		public void setStop(A xStop, A yStop){
			this.xStop = xStop;
			this.yStop = yStop;
		}
		
		public Boolean isStartEmpty(){
			return (this.xStart == null && this.yStart == null);
			
		}
		public Boolean isStopEmpty(){
			return (this.xStop == null && this.yStop == null);
			
		}
		
		/**
		 * This class swaps X and Y- Coordinates! (May be useful in some casses)
		 */
		public void swap(){
			A tmp = this.xStart;
			this.xStart = yStart;
			this.yStart = tmp;
			
			tmp = this.xStop;
			this.xStop = yStop;
			this.yStop = tmp;
		}
	}