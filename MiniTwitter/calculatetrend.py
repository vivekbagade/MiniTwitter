#! /usr/bin/env python
import cql
from datetime import datetime,timedelta
import time
from time import mktime
from datetime import datetime
import psycopg2
import sys


dbcon = None

f = open('calculatetrends_logfile.txt', 'a')
f.write('\n\n\n\n\n----The python script is being executed at\n')
currenttime = datetime.time(datetime.now());
f.write("".join([str(currenttime),'\n']))

try:
     
    dbcon = psycopg2.connect("dbname='vivek' user='vivek' host='172.16.155.82' password='123'")
    print (dbcon)
    cur = dbcon.cursor()
    cur.execute('SELECT version()')          
    ver = cur.fetchone()
    print ver    
    

except psycopg2.DatabaseError, e:
    print 'Error %s' % e    
    sys.exit(1)




try:
    strptime = time.strptime
except AttributeError:
    from strptime import strptime

host, port, keyspace = "172.16.155.102", 9160, 'MiniTwitter'
con = cql.connect(host, port,keyspace,cql_version='3.0.0')
 
f.write (str(con))
print(keyspace)
cursor = con.cursor()
CQL_query = 'USE "MiniTwitter"';
r =cursor.execute(CQL_query)
f.write(str(r))
CQL_query = 'SELECT * FROM trends';
r= cursor.execute(CQL_query)
f.write(str(r))

#Delete all the entries of the trends table as we have to calculate trends based on the past hour data
if cursor.rowcount > 1:
	f.write('\nDeleting the existing rows of the table\n')
	cur.execute("TRUNCATE trends")
	f.write(str(cur))
	dbcon.commit()
	cur.execute("SELECT * FROM trends")
	f.write("".join(["\n----Rows after deletion---\n",str(cur.rowcount),"\n"])) 



twohoursbefore = (datetime.now() - timedelta(0,2*60*60))
onehourbefore = (datetime.now() - timedelta(0,1*60*60))



for row in cursor:  # Iteration is equivalent to lots of fetchone() calls	
	
	
	entrytime = strptime(row[0], "2013-%m-%d %H:%M")
	entrydatetime = datetime.fromtimestamp(mktime(entrytime))
	#print entrydatetime.hour;
	#if entrydatetime.hour == twohoursbefore.hour and entrydatetime.hour < onehourbefore.hour:
	f.write ("".join([row[1],str(row[2])]))  
	

	cur.execute("SELECT * FROM trends WHERE word = %s", (row[1],))
	print(cur)	
	if cur.rowcount == 0:
		#means the row has to be inserted
		f.write('\nWord does not exist. Will now be inserted.\n')		
		cur.execute("INSERT INTO trends (word, count) VALUES (%s, %s)", (row[1], row[2],))
		f.write(str(cur))		
		dbcon.commit()
	else:
		#means the word count has to be updated
		word_row = cur.fetchone()		
		f.write('\nWord exists. count will be updated.\n')
		f.write ("".join(["\nThe existing entry in database:\n",(word_row[1])]))		
		f.write ("".join(['\nCurrent Count\n',str(word_row[0])]))
		
		
		count = int(word_row[0]) + row[2]
		cur.execute("UPDATE trends SET count=%s WHERE word=%s", (count, word_row[1]))
 		f.write(str(cur))
		dbcon.commit()		
print 'Database successfully updated'
f.write('\nDatabase successfully updated\n')

cursor.execute("SELECT * FROM trends")
#Deleting the rows cassandra database
for row in cursor:
	deletioncursor = con.cursor()
	deletioncursor.execute("SELECT * FROM trends WHERE KEY=:timestamp",dict(timestamp=row[0]))
	
	if deletioncursor.rowcount > 0:	
		r= deletioncursor.execute("DELETE FROM trends WHERE KEY=:timestamp",dict(timestamp=row[0]))
		f.write('---Deletion Operation----\n')
		f.write(str(r))


