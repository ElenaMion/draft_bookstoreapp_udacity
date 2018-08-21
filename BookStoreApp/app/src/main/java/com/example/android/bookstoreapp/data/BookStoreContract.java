package com.example.android.bookstoreapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookStoreContract {

    public BookStoreContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.bookstoreapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BOOKS = "books";
    public static final String PATH_SUPPLIERS = "suppliers";
    public static final String PATH_DELIVERIES = "deliveries";

    /**
     * Table for books
     */
    public static final class BookEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * Name of database table for books
         */
        public final static String TABLE_NAME = "books";

        /**
         * Unique ID number for the book
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the book.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_BOOK_NAME = "book_name";

        /**
         * Author of the book.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_BOOK_AUTHOR = "author";

        /**
         * Price for the book.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_PRICE = "price";

        /**
         * Number of books in stock
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_QUANTITY_IN_STOCK = "quantity_in_stock";

        /**
         * Genre of the book. Possible values defined below.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_GENRE = "genre";

        /**
         * Possible values for the genre of the book.
         */
        public static final int GENRE_UNKNOWN = 0;
        public static final int GENRE_FANTASY = 1;
        public static final int GENRE_SCI_FI = 2;
        public static final int GENRE_HORROR = 3;
        public static final int GENRE_ADVENTURE = 4;
        public static final int GENRE_ROMANCE = 5;
        public static final int GENRE_DRAMA = 6;

        public static boolean isValidGenre(int genre) {
            if (genre == GENRE_UNKNOWN || genre == GENRE_FANTASY || genre == GENRE_SCI_FI ||
                    genre == GENRE_HORROR || genre == GENRE_ADVENTURE || genre == GENRE_ROMANCE || genre == GENRE_DRAMA) {
                return true;
            }
            return false;
        }
    }

    /**
     * Table for suppliers
     */
    public static final class SupplierEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SUPPLIERS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIERS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIERS;

        /**
         * Name of database table for suppliers
         */
        public final static String TABLE_NAME = "suppliers";

        /**
         * Unique ID number for the supplier
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the supplier.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";

        /**
         * Phone of the supplier.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_PHONE = "phone";

        /**
         * Address of the supplier.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_ADDRESS = "address";
    }

    /**
     * Table for deliveries
     */
    public static final class DeliveryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_DELIVERIES);
        public static final Uri CONTENT_URI_WITH_BOOK = Uri.withAppendedPath(CONTENT_URI, PATH_BOOKS);
        public static final Uri CONTENT_URI_WITH_SUPPLIER = Uri.withAppendedPath(CONTENT_URI, PATH_SUPPLIERS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DELIVERIES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DELIVERIES;

        public static final String CONTENT_ITEM_TYPE_WITH_BOOK =
                CONTENT_ITEM_TYPE + "/" + PATH_BOOKS;

        public static final String CONTENT_ITEM_TYPE_WITH_SUPPLIER =
                CONTENT_ITEM_TYPE + "/" + PATH_SUPPLIERS;

        /**
         * Name of database table for deliveries
         */
        public final static String TABLE_NAME = "deliveries";

        /**
         * Unique ID number for the delivery
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Book ID number for the delivery - foreign key
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_BOOK_ID = "book_id";

        /**
         * Supplier ID number for the delivery - foreign key
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_SUPPLIER_ID = "supplier_id";

        /**
         * Date of the delivery
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_DATE = "date";

        /**
         * Number of books delivered
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_QUANTITY_DELIVERED = "quantity_delivered";
    }
}
