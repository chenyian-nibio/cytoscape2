//LinearNumberInterpolator.java

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.vizmap;
//----------------------------------------------------------------------------
/**
 * This subclass of NumberInterpolator further assumes a linear interpolation,
 * and calculates the fractional distance of the target domain value from
 * the lower boundary value for the convenience of subclasses.
 */
abstract public class LinearNumberInterpolator extends NumberInterpolator {

    public Object getRangeValue(double lowerDomain, Object lowerRange,
				double upperDomain, Object upperRange,
				double domainValue) {
	if (lowerDomain == upperDomain) {return lowerRange;}
	double frac = (domainValue - lowerDomain) / (upperDomain-lowerDomain);
	return getRangeValue(frac, lowerRange, upperRange);
    }

    abstract public Object getRangeValue(double frac, Object lowerRange,
					 Object upperRange);
}


