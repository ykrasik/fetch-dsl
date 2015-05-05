What is ORM-Fetch-DSL?
===
A DSL for instructing ORM queries which columns to fetch eagerly.  
  
Most ORM implementations operate with partial objects - the same object can be re-used in multiple queries, but it
is fetched partially - every query specifies which fields of the object (mapped to columns in the table) to fetch.  
Access to any field that wasn't explicitly pre-fetched will cause the ORM to load it lazily from the DB.  
To prevent this lazy loading, the ORM has to be instructed up front which columns to fetch for every query.  
The eager-fetch configuration can become very deep (for tables with lots of joins) and cumbersome to maintain,
and this tries to alleviate the strain a bit.  
  
Disclaimer
===
This is admittedly geared towards JPA-style ORMs, or any ORM which represents tables as entity classes that store
database metadata as annotations on the fields themselves.  

Structure
---
There is a core module 'fetch-dsl-core', which is independent of any specific ORM implementation.  
It simply translates the DSL into a parent-child tree.  
ORM-specific implementations are expected to know how to apply this relation to their queries.  
  
## Supported ORMs:
Currently, only Ebean ORM is supported.
  
Syntax
===

## Fetch Descriptor
The basic unit of work is a Fetch Descriptor, which from now will be referred to simply as 'descriptor'.  
A descriptor is simply a fetch configuration with a name. It describes the columns that should be eagerly fetched,
and can have a nested parent-child structure that signifies joins and the columns to be fetched from the joined table.  
  
Each descriptor has a *'base table'*. When applied to a query, the descriptor will always be applied to the *base table*
and any joined tables are joined from the *base table*.

### Fetching columns
To fetch a column, simply write the name of the column.  
If the name of the column is a reserved Java or Groovy keyword, prefix it with a '_'. The leading underscore will be ignored.
This limitation also means that columns starting with '_' cannot be fetched.  
    ```
    descriptor('desc') {
        column_1,
        _public,
        column_2
    }
    ```
This will create a descriptor that, when applied to a query, will instruct the query to fetch the columns 'column_1',
'public' and 'column2' from the base table.

### Joining tables
To join a column with another table and instruct the descriptor which columns to fetch from the joined table, simply
write the name of the column, followed by a curly bracket block with more column names.  
The column is expected to be a foreign key to some table (this information is expected to be stored in the implementation's
metadata - as mentioned, geared for JPA).  
The depth of nesting (joining) is unlimited.
    ```
    descriptor('desc') {
        column_1 {
            column_2 {
                column_3
            }
            column4
        }
        column5
    }
    ```
This will create a descriptor that, when applied to a query, will:
  1. Instruct the query to fetch the columns 'column_1', 'column_5' eagerly from the base table (which may 
     contain other columns, which will be fetched lazily).
  2. Instruct the query to join the column 'column_1' to the table to which it is a foreign key, and from that
     table fetch the columns 'column_2' and 'column_4'.
  3. Instruct the query to join the column 'column_1' to the table to which it is a foreign key, then join 'column_2'
     to the table to which it, in turn, is a foreign key, and from that table fetch the column 'column_3'.

### Fetching single column from join
There is a shorthand for joining a table and only fetching 1 column - just write the join column, followed by the name
of the single column to fetch from the joined table, surrounded in single or double quotes.
    ```
    descriptor('desc') {
        column_1 'column_2'
    }
    ```
This will create a descriptor that, when applied to a query, will instruct the query to fetch the column 'column_1',
join it to the table to which it is a foreign key, and from that joined table fetch the column 'column_2'.
Note that any column passed like this **must** be surrounded by single or double quotes.

### Descriptor references
Sometimes, it is desirable to re-use descriptors, or simply to avoid deep levels of nesting for tables with deep joins.
It is possible to join a column to a table and fetch the joined table according to another descriptor, which is defined
somewhere else in the same descriptor definition file, or in a different file altogether.
This allows you to avoid cluttering all your descriptors into a single file.
The syntax is similar to the shorthand single column join mentioned above, but the descriptor name must start with a '#'.
    ```
    descriptor('desc') {
        column_1 '#desc2'
    }
    
    descriptor('desc2') {
        column_2
    }
    ```
This will create 2 descriptors. 'desc2' is a simple, stand-alone descriptor, but we will focus on 'desc1':
When applied to a query, it will instruct the query to fetch 'column_1' and join it to the table to which it is a 
foreign key, and fetch that table with the descriptor 'desc2', which in this case means fetch 'column_2'.
Note that the descriptor **must** be surrounded by single or double quotes, and start with a '#'.

### Full Example
    ```
    descriptor('desc') {
        column_1
        column_2 'child_2'
        column_3 {
            child_3 {
                another_child
            }
            some_child
        }
        column_4 '#desc2'
    }
 
    descriptor('desc2') {
        column_5
    }
    ``` 
                    
Getting it to work
===
This is how you would integrate this with Ebean:
 1. Create descriptor definition files(.groovy) with the above syntax and place them somewhere in your resources dir.
 2. Load the descriptor definition files to an EbeanFetchDescriptorManager.
    ```
    final EbeanFetchDescriptorManagerBuilder builder = new EbeanFetchDescriptorManagerBuilder();
    builder.loadFile(new File("path/to/file.groovy");
    builder.loadUrl(getClass().getResource("descriptors.groovy");
    
    final EbeanFetchDescriptorManager manager = builder.build();
    ```
 3. Apply your descriptors to your queries through the manager.
    ```
    final Query<Order> orderQuery = Ebean.find(Order.class)
    manager.apply(orderQuery, "desc");
    ```