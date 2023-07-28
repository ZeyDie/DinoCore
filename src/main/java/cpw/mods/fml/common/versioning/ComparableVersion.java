/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common.versioning;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.math.BigInteger;
import java.util.*;

/**
 * Generic implementation of version comparison.
 *
 * <p>Features:
 * <ul>
 * <li>mixing of '<code>-</code>' (dash) and '<code>.</code>' (dot) separators,</li>
 * <li>transition between characters and digits also constitutes a separator:
 *     <code>1.0alpha1 =&gt; [1, 0, alpha, 1]</code></li>
 * <li>unlimited number of version components,</li>
 * <li>version components in the text can be digits or strings,</li>
 * <li>strings are checked for well-known qualifiers and the qualifier ordering is used for version ordering.
 *     Well-known qualifiers (case insensitive) are:<ul>
 *     <li><code>snapshot</code></li>
 *     <li><code>alpha</code> or <code>a</code></li>
 *     <li><code>beta</code> or <code>b</code></li>
 *     <li><code>milestone</code> or <code>m</code></li>
 *     <li><code>rc</code> or <code>cr</code></li>
 *     <li><code>(the empty string)</code> or <code>ga</code> or <code>final</code></li>
 *     <li><code>sp</code></li>
 *     </ul>
 *     Unknown qualifiers are considered after known qualifiers, with lexical order (always case insensitive),
 *   </li>
 * <li>a dash usually precedes a qualifier, and is always less important than something preceded with a dot.</li>
 * </ul></p>
 *
 * @see <a href="https://cwiki.apache.org/confluence/display/MAVENOLD/Versioning">"Versioning" on Maven Wiki</a>
 * @author <a href="mailto:kenney@apache.org">Kenney Westerhof</a>
 * @author <a href="mailto:hboutemy@apache.org">Hervé Boutemy</a>
 */
public class ComparableVersion
    implements Comparable<ComparableVersion>
{
    private String value;

    private String canonical;

    private ListItem items;

    private interface Item
    {
        final int INTEGER_ITEM = 0;
        final int STRING_ITEM = 1;
        final int LIST_ITEM = 2;

        int compareTo( Item item );

        int getType();

        boolean isNull();
    }

    /**
     * Represents a numeric item in the version item list.
     */
    private static class IntegerItem
        implements Item
    {
    	private static final BigInteger BigInteger_ZERO = new BigInteger( "0" );

        private final BigInteger value;

        public static final IntegerItem ZERO = new IntegerItem();

        private IntegerItem()
        {
            this.value = BigInteger_ZERO;
        }

        public IntegerItem(final String str )
        {
            this.value = new BigInteger( str );
        }

        public int getType()
        {
            return INTEGER_ITEM;
        }

        public boolean isNull()
        {
            return BigInteger_ZERO.equals( value );
        }

        public int compareTo(final Item item )
        {
            if ( item == null )
            {
                return BigInteger_ZERO.equals( value ) ? 0 : 1; // 1.0 == 1, 1.1 > 1
            }

            switch ( item.getType() )
            {
                case INTEGER_ITEM:
                    return value.compareTo( ( (IntegerItem) item ).value );

                case STRING_ITEM:
                    return 1; // 1.1 > 1-sp

                case LIST_ITEM:
                    return 1; // 1.1 > 1-1

                default:
                    throw new RuntimeException( "invalid item: " + item.getClass() );
            }
        }

        public String toString()
        {
            return value.toString();
        }
    }

    /**
     * Represents a string in the version item list, usually a qualifier.
     */
    private static class StringItem
        implements Item
    {
        private static final String[] QUALIFIERS = { "alpha", "beta", "milestone", "rc", "snapshot", "", "sp" };

        private static final List<String> _QUALIFIERS = Arrays.asList( QUALIFIERS );

        private static final Properties ALIASES = new Properties();
        static
        {
            ALIASES.put( "ga", "" );
            ALIASES.put( "final", "" );
            ALIASES.put( "cr", "rc" );
        }

        /**
         * A comparable value for the empty-string qualifier. This one is used to determine if a given qualifier makes
         * the version older than one without a qualifier, or more recent.
         */
        private static final String RELEASE_VERSION_INDEX = String.valueOf( _QUALIFIERS.indexOf( "" ) );

        private String value;

        public StringItem(String value, final boolean followedByDigit )
        {
            String value1 = value;
            if ( followedByDigit && value1.length() == 1 )
            {
                // a1 = alpha-1, b1 = beta-1, m1 = milestone-1
                switch ( value1.charAt( 0 ) )
                {
                    case 'a':
                        value1 = "alpha";
                        break;
                    case 'b':
                        value1 = "beta";
                        break;
                    case 'm':
                        value1 = "milestone";
                        break;
                }
            }
            this.value = ALIASES.getProperty(value1, value1);
        }

        public int getType()
        {
            return STRING_ITEM;
        }

        public boolean isNull()
        {
            return ( comparableQualifier( value ).compareTo( RELEASE_VERSION_INDEX ) == 0 );
        }

        /**
         * Returns a comparable value for a qualifier.
         *
         * This method takes into account the ordering of known qualifiers then unknown qualifiers with lexical ordering.
         *
         * just returning an Integer with the index here is faster, but requires a lot of if/then/else to check for -1
         * or QUALIFIERS.size and then resort to lexical ordering. Most comparisons are decided by the first character,
         * so this is still fast. If more characters are needed then it requires a lexical sort anyway.
         *
         * @param qualifier
         * @return an equivalent value that can be used with lexical comparison
         */
        public static String comparableQualifier(final String qualifier )
        {
            final int i = _QUALIFIERS.indexOf( qualifier );

            return i == -1 ? ( _QUALIFIERS.size() + "-" + qualifier ) : String.valueOf( i );
        }

        public int compareTo(final Item item )
        {
            if ( item == null )
            {
                // 1-rc < 1, 1-ga > 1
                return comparableQualifier( value ).compareTo( RELEASE_VERSION_INDEX );
            }
            switch ( item.getType() )
            {
                case INTEGER_ITEM:
                    return -1; // 1.any < 1.1 ?

                case STRING_ITEM:
                    return comparableQualifier( value ).compareTo( comparableQualifier( ( (StringItem) item ).value ) );

                case LIST_ITEM:
                    return -1; // 1.any < 1-1

                default:
                    throw new RuntimeException( "invalid item: " + item.getClass() );
            }
        }

        public String toString()
        {
            return value;
        }
    }

    /**
     * Represents a version list item. This class is used both for the global item list and for sub-lists (which start
     * with '-(number)' in the version specification).
     */
    private static class ListItem
        extends ArrayList<Item>
        implements Item
    {
        public int getType()
        {
            return LIST_ITEM;
        }

        public boolean isNull()
        {
            return ( size() == 0 );
        }

        void normalize()
        {
            for(final ListIterator<Item> iterator = listIterator( size() ); iterator.hasPrevious(); )
            {
                final Item item = iterator.previous();
                if ( item.isNull() )
                {
                    iterator.remove(); // remove null trailing items: 0, "", empty list
                }
                else
                {
                    break;
                }
            }
        }

        public int compareTo(final Item item )
        {
            if ( item == null )
            {
                if ( size() == 0 )
                {
                    return 0; // 1-0 = 1- (normalize) = 1
                }
                final Item first = get( 0 );
                return first.compareTo( null );
            }
            switch ( item.getType() )
            {
                case INTEGER_ITEM:
                    return -1; // 1-1 < 1.0.x

                case STRING_ITEM:
                    return 1; // 1-1 > 1-sp

                case LIST_ITEM:
                    final Iterator<Item> left = iterator();
                    final Iterator<Item> right = ( (ListItem) item ).iterator();

                    while ( left.hasNext() || right.hasNext() )
                    {
                        final Item l = left.hasNext() ? left.next() : null;
                        final Item r = right.hasNext() ? right.next() : null;

                        // if this is shorter, then invert the compare and mul with -1
                        final int result = l == null ? -1 * r.compareTo( l ) : l.compareTo( r );

                        if ( result != 0 )
                        {
                            return result;
                        }
                    }

                    return 0;

                default:
                    throw new RuntimeException( "invalid item: " + item.getClass() );
            }
        }

        public String toString()
        {
            final StringBuilder buffer = new StringBuilder( "(" );
            for(final Iterator<Item> iter = iterator(); iter.hasNext(); )
            {
                buffer.append( iter.next() );
                if ( iter.hasNext() )
                {
                    buffer.append( ',' );
                }
            }
            buffer.append( ')' );
            return buffer.toString();
        }
    }

    public ComparableVersion(final String version )
    {
        parseVersion( version );
    }

    public final void parseVersion( String version )
    {
        this.value = version;

        items = new ListItem();

        String version1 = version.toLowerCase(Locale.ENGLISH);

        ListItem list = items;

        final Stack<Item> stack = new Stack<Item>();
        stack.push( list );

        boolean isDigit = false;

        int startIndex = 0;

        for (int i = 0; i < version1.length(); i++ )
        {
            final char c = version1.charAt( i );

            if ( c == '.' )
            {
                if ( i == startIndex )
                {
                    list.add( IntegerItem.ZERO );
                }
                else
                {
                    list.add( parseItem( isDigit, version1.substring( startIndex, i ) ) );
                }
                startIndex = i + 1;
            }
            else if ( c == '-' )
            {
                if ( i == startIndex )
                {
                    list.add( IntegerItem.ZERO );
                }
                else
                {
                    list.add( parseItem( isDigit, version1.substring( startIndex, i ) ) );
                }
                startIndex = i + 1;

                if ( isDigit )
                {
                    list.normalize(); // 1.0-* = 1-*

                    if ( ( i + 1 < version1.length() ) && Character.isDigit( version1.charAt( i + 1 ) ) )
                    {
                        // new ListItem only if previous were digits and new char is a digit,
                        // ie need to differentiate only 1.1 from 1-1
                        list.add( list = new ListItem() );

                        stack.push( list );
                    }
                }
            }
            else if ( Character.isDigit( c ) )
            {
                if ( !isDigit && i > startIndex )
                {
                    list.add( new StringItem( version1.substring( startIndex, i ), true ) );
                    startIndex = i;
                }

                isDigit = true;
            }
            else
            {
                if ( isDigit && i > startIndex )
                {
                    list.add( parseItem( true, version1.substring( startIndex, i ) ) );
                    startIndex = i;
                }

                isDigit = false;
            }
        }

        if ( version1.length() > startIndex )
        {
            list.add( parseItem( isDigit, version1.substring( startIndex ) ) );
        }

        while ( !stack.isEmpty() )
        {
            list = (ListItem) stack.pop();
            list.normalize();
        }

        canonical = items.toString();
    }

    private static Item parseItem(final boolean isDigit, final String buf )
    {
        return isDigit ? new IntegerItem( buf ) : new StringItem( buf, false );
    }

    public int compareTo(final ComparableVersion o )
    {
        return items.compareTo( o.items );
    }

    public String toString()
    {
        return value;
    }

    public boolean equals(final Object o )
    {
        return ( o instanceof ComparableVersion ) && canonical.equals( ( (ComparableVersion) o ).canonical );
    }

    public int hashCode()
    {
        return canonical.hashCode();
    }
}
