/**
 * This class contains information about a speedSign
 * @author MH
 *
 * @param <A> (usually Integer!)
 */
public class SpeedObject<A> {
		private A xCenter;
		private A yCenter;
		private A radius;
		private A offsetRadius;
		private A speed;

		public SpeedObject(A xCenter, A yCenter, A radius, A offsetRadius, A speed) {
			super();
			this.xCenter = xCenter;
			this.yCenter = yCenter;
			this.radius = radius;
			this.offsetRadius = offsetRadius;
			this.speed = speed;

		}
		
		public SpeedObject() {
			super();
			this.xCenter = null;
			this.yCenter = null;
			this.radius = null;
			this.offsetRadius = null;
			this.speed = null;
		}
		
		public boolean equalsCenter(Object other) {
			if (other instanceof SpeedObject) {
				SpeedObject<?> otherCoordinates = (SpeedObject<?>) other;
				
				Boolean isCenterEq = ((this.xCenter == otherCoordinates.xCenter
						|| (this.xCenter != null && otherCoordinates.xCenter != null && this.xCenter.equals(otherCoordinates.xCenter)))
						&& (this.yCenter == otherCoordinates.yCenter || (this.yCenter != null && otherCoordinates.yCenter != null
								&& this.yCenter.equals(otherCoordinates.yCenter))));
				
				return isCenterEq;
			}

			return false;
		}
		

		public String toString() {
			return "(" + xCenter + "," + yCenter + "), Radius:" + radius + " +/-" + offsetRadius + " Speed:" + speed;
		}

		public A getxCenter() {
			return this.xCenter;
		}
		public A getyCenter() {
			return this.yCenter;
		}
		public A getRadius() {
			return this.radius;
		}
		public A getOffset() {
			return this.offsetRadius;
		}
		
		public A getSpeed(){
			return this.speed;
		}
		
		public void setCenter(A xCenter, A yCenter){
			this.xCenter = xCenter;
			this.yCenter = yCenter;
		}
		
		public void setRadius(A radius, A offsetRadius){
			this.radius = radius;
			this.offsetRadius = offsetRadius;
		}
		
		public Boolean isCenterEmpty(){
			return (this.xCenter == null && this.yCenter == null);		
		}
		
		/**
		 * This class swaps X and Y- Coordinates! (May be useful in some cases)
		 */
		public void swap(){
			A tmp = this.xCenter;
			this.xCenter = yCenter;
			this.yCenter = tmp;
		}
	}