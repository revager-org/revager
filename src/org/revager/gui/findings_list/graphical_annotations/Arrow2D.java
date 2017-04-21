/*
 * Geotools - OpenSource mapping toolkit
 * (C) 2001, Institut de Recherche pour le Développement
 * (C) 1998, Pêches et Océans Canada
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 * Contacts:
 *     UNITED KINGDOM: James Macgill
 *             mailto:j.macgill@geog.leeds.ac.uk
 *
 *     FRANCE: Surveillance de l'Environnement Assistée par Satellite
 *             Institut de Recherche pour le Développement / US-Espace
 *             mailto:seasnet@teledetection.fr
 *
 *     CANADA: Observatoire du Saint-Laurent
 *             Institut Maurice-Lamontagne
 *             mailto:osl@osl.gc.ca
 */
package org.revager.gui.findings_list.graphical_annotations;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.NoSuchElementException;

public class Arrow2D extends RectangularShape {

	private double minX, minY;

	private double length;

	private double thickness;

	private double sy0 = 0;

	private double sy1 = 1.0 / 3;

	private double sx = 2.0 / 3;

	public Arrow2D() {
	}

	public Arrow2D(final double x, final double y, final double width, final double height) {
		this.minX = x;
		this.minY = y;
		this.length = width;
		this.thickness = height;
	}

	public void setTailProportion(double sx, double sy1, double sy0) {
		if (sy1 < 0)
			sy1 = 0;
		if (sy1 > 1)
			sy1 = 1;
		if (sy0 < 0)
			sy0 = 0;
		if (sy0 > 1)
			sy0 = 1;
		if (sx < 0)
			sx = 0;
		if (sx > 1)
			sx = 1;

		this.sy1 = sy1;
		this.sy0 = sy0;
		this.sx = sx;
	}

	public double getTailLength() {
		return sx * length;
	}

	@Override
	public double getX() {
		return minX;
	}

	@Override
	public double getY() {
		return minY;
	}

	@Override
	public double getWidth() {
		return length;
	}

	@Override
	public double getHeight() {
		return thickness;
	}

	public double getHeight(double x) {
		x = (x - minX) / (sx * length);
		if (x < 0 || x > 1) {
			return 0;
		} else if (x <= 1) {
			return (sy0 + (sy1 - sy0) * x) * thickness;
		} else {
			return (x - 1) * sx / (1 - sx) * thickness;
		}
	}

	@Override
	public boolean isEmpty() {
		return !(length > 0 && thickness > 0);
	}

	@Override
	public void setFrame(final double x, final double y, final double width, final double height) {
		this.minX = x;
		this.minY = y;
		this.length = width;
		this.thickness = height;
	}

	@Override
	public Rectangle2D getBounds2D() {
		return new Rectangle2D.Double(minX, minY, length, thickness);
	}

	@Override
	public boolean contains(final double x, double y) {
		if (x < minX) {
			return false;
		}
		final double base = minX + sx * length;
		if (x <= base) {
			/*
			 * Point dans la queue. V�rifie s'il se trouve dans le triangle...
			 */
			double yMaxAtX = 0.5 * thickness;
			y -= (minY + yMaxAtX);
			yMaxAtX *= sy0 + (sy1 - sy0) * ((x - minX) / (base - minX));
			return (Math.abs(y) <= yMaxAtX);
		} else {
			/*
			 * Point dans la pointe. V�rifie s'il se trouve dans le triangle.
			 */
			final double maxX = minX + length;
			if (x > maxX) {
				return false;
			}
			double yMaxAtX = 0.5 * thickness;
			y -= (minY + yMaxAtX);
			yMaxAtX *= (maxX - x) / (maxX - base);
			return (Math.abs(y) <= yMaxAtX);
		}
	}

	@Override
	public boolean contains(final double x, final double y, final double width, final double height) {
		return contains(x, y) && contains(x + width, y) && contains(x + width, y + height) && contains(x, y + height);
	}

	@Override
	public boolean intersects(final double x, final double y, final double width, final double height) {
		final double right = x + width;
		final double maxX = minX + length;
		if (x <= maxX && right >= minX) {
			final double top = y + height;
			final double maxY = minY + thickness;
			if (y <= maxY && top >= minY) {
				/*
				 * The rectangle intersects this arrow's bounding box. Now,
				 * check if a rectangle corner is outside the arrow (while in
				 * the bounding box). If such a case is found, returns false.
				 */
				final double base = minX + length * sx;
				if (x > base) {
					double yMaxAtX = 0.5 * thickness;
					final double centerY = minY + yMaxAtX;
					if (y >= centerY) {
						yMaxAtX *= (maxX - x) / (maxX - base);
						if (!(y - centerY <= yMaxAtX)) {
							return false;
						}
					} else if (top <= centerY) {
						yMaxAtX *= (maxX - x) / (maxX - base);
						if (!(centerY - top <= yMaxAtX)) {
							return false;
						}
					}
				} else if (right < base) {
					double yMaxAtX = 0.5 * thickness;
					final double centerY = minY + yMaxAtX;
					if (y >= centerY) {
						yMaxAtX *= sy0 + (sy1 - sy0) * ((x - minX) / (base - minX));
						if (!(y - centerY <= yMaxAtX)) {
							return false;
						}
					} else if (top <= centerY) {
						yMaxAtX *= sy0 + (sy1 - sy0) * ((x - minX) / (base - minX));
						if (!(centerY - top <= yMaxAtX)) {
							return false;
						}
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public PathIterator getPathIterator(final AffineTransform at, final double flatness) {
		return new Iterator(at);
	}

	@Override
	public PathIterator getPathIterator(final AffineTransform at) {
		return new Iterator(at);
	}

	private class Iterator implements PathIterator {

		private final AffineTransform at;

		private final double halfBottom0, halfBottom1, center, halfTop1, halfTop0, base;

		private int code;

		Iterator(final AffineTransform at)

		{
			this.at = at;
			final double halfheight = 0.5 * thickness;
			halfBottom0 = minY + halfheight * (1 - sy0);
			halfBottom1 = minY + halfheight * (1 - sy1);
			center = minY + halfheight;
			halfTop1 = minY + halfheight * (1 + sy1);
			halfTop0 = minY + halfheight * (1 + sy0);
			base = minX + sx * length;
		}

		@Override
		public int getWindingRule() {
			return WIND_EVEN_ODD;
		}

		@Override
		public void next() {
			code++;
		}

		@Override
		public int currentSegment(final float[] coords) {
			switch (code) {
			case 0:
				coords[0] = (float) minX;
				coords[1] = (float) halfBottom0;
				break;
			case 1:
				coords[0] = (float) base;
				coords[1] = (float) halfBottom1;
				break;
			case 2:
				coords[0] = (float) base;
				coords[1] = (float) minY;
				break;
			case 3:
				coords[0] = (float) (minX + length);
				coords[1] = (float) center;
				break;
			case 4:
				coords[0] = (float) base;
				coords[1] = (float) (minY + thickness);
				break;
			case 5:
				coords[0] = (float) base;
				coords[1] = (float) halfTop1;
				break;
			case 6:
				coords[0] = (float) minX;
				coords[1] = (float) halfTop0;
				break;
			case 7:
				coords[0] = (float) minX;
				coords[1] = (float) halfBottom0;
				break;
			case 8:
				return SEG_CLOSE;
			default:
				throw new NoSuchElementException();
			}
			if (at != null) {
				at.transform(coords, 0, coords, 0, 1);
			}
			return (code == 0) ? SEG_MOVETO : SEG_LINETO;
		}

		@Override
		public int currentSegment(final double[] coords) {
			switch (code) {
			case 0:
				coords[0] = minX;
				coords[1] = halfBottom0;
				break;
			case 1:
				coords[0] = base;
				coords[1] = halfBottom1;
				break;
			case 2:
				coords[0] = base;
				coords[1] = minY;
				break;
			case 3:
				coords[0] = minX + length;
				coords[1] = center;
				break;
			case 4:
				coords[0] = base;
				coords[1] = minY + thickness;
				break;
			case 5:
				coords[0] = base;
				coords[1] = halfTop1;
				break;
			case 6:
				coords[0] = minX;
				coords[1] = halfTop0;
				break;
			case 7:
				coords[0] = minX;
				coords[1] = halfBottom0;
				break;
			case 8:
				return SEG_CLOSE;
			default:
				throw new NoSuchElementException();
			}
			if (at != null) {
				at.transform(coords, 0, coords, 0, 1);
			}
			return (code == 0) ? SEG_MOVETO : SEG_LINETO;
		}

		@Override
		public boolean isDone() {
			return code > 8;
		}

	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj != null && getClass().equals(obj.getClass())) {
			final Arrow2D cast = (Arrow2D) obj;
			return Double.doubleToLongBits(thickness) == Double.doubleToLongBits(cast.thickness)
					&& Double.doubleToLongBits(length) == Double.doubleToLongBits(cast.length)
					&& Double.doubleToLongBits(minX) == Double.doubleToLongBits(cast.minX)
					&& Double.doubleToLongBits(minY) == Double.doubleToLongBits(cast.minY)
					&& Double.doubleToLongBits(sx) == Double.doubleToLongBits(cast.sx)
					&& Double.doubleToLongBits(sy0) == Double.doubleToLongBits(cast.sy1)
					&& Double.doubleToLongBits(sy1) == Double.doubleToLongBits(cast.sy0);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final long code = Double.doubleToLongBits(thickness)
				+ 37 * (Double.doubleToLongBits(length) + 37 * (Double.doubleToLongBits(minX)
						+ 37 * (Double.doubleToLongBits(minY) + 37 * (Double.doubleToLongBits(sx)
								+ 37 * (Double.doubleToLongBits(sy0) + 37 * (Double.doubleToLongBits(sy1)))))));
		return (int) code + (int) (code >>> 32);
	}

}