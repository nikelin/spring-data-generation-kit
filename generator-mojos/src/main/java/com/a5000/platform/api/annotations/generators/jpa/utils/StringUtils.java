package com.a5000.platform.api.annotations.generators.jpa.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


@SuppressWarnings("restriction")
public final class StringUtils {
    public static List<String> camelCaseDelimiters = Arrays.asList("_", "-");
	private static final String RANDOM_STRING_SOURCE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	public static String trim( String source ) {
		return trim( source, " ");
	}

	public static String trim( String source, String needle ) {
		if ( source == null ) {
			throw new IllegalArgumentException("<null>");
		}

		while ( source.startsWith(needle) ) {
			source = source.substring(1);
		}

		while ( source.endsWith(needle) ) {
			source = source.substring(0, source.length() - 1 );
		}

		return source;
	}

    public static String toCamelCase( String name ) {
        return toCamelCase( name, true );
    }

    /**
	* Camelize input string
	* @param name Input string
	* @param ucfirst Make first character uppercased
	* @return String
	*/
    public static String toCamelCase( String name, boolean ucfirst ) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < name.length(); i++) {
            String prevChar = name.substring(i > 0 ? i - 1 : 0, i > 0 ? i : 1);
            String currChar = name.substring(i, i + 1);

            if ( camelCaseDelimiters.contains( prevChar ) || ( ucfirst && i == 0 && !camelCaseDelimiters.contains( currChar ) ) ) {
                result.append( currChar.toUpperCase() );
            } else if ( !camelCaseDelimiters.contains( currChar ) ) {
                result.append(currChar);
            }
        }

        return result.toString();
    }

    public static String fromCamelCase( String name, String delimiter ) {
        StringBuilder result = new StringBuilder();

        int last_delimiter_pos = 0;
        for ( int i = 0; i < name.length(); i++ ) {
            String currChar = name.substring(i, i + 1);

            if ( currChar.toUpperCase().equals( currChar ) && i != last_delimiter_pos - 1 ) {
                if ( i > 0 ) {
                    result.append( delimiter );
                    last_delimiter_pos = i;
                }

                result.append( currChar.toLowerCase() );
            } else {
                result.append( currChar.toLowerCase() );
            }
        }

        return result.toString();
    }

    public static String join( Collection<?> join, String separator ) {
		return join( join.toArray(), separator );
    }

    public static String join( Object[] join, String separator ) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for ( Object joinItem : join ) {
            if ( joinItem == null ) {
                continue;
            }

			builder.append( joinItem );

			if ( i++ != join.length - 1 ) {
				builder.append( separator );
			}
        }

        return builder.toString();
    }

    public static String ucfirst( String value ) {
		if ( value.isEmpty() ) {
			return value;
		}

    	return value.substring(0, 1).toUpperCase().concat( value.substring(1) );
    }

    public static String lcfirst( String value ) {
		if ( value.isEmpty() ) {
			return value;
		}

    	return value.substring(0, 1).toLowerCase().concat( value.substring(1) );
    }

    
}
