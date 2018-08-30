import time
import Adafruit_ADS1x15
import csv
import sys

adc = Adafruit_ADS1x15.ADS1015()
GAIN = 16
DATARATE = 3300

#with open('measuremtents.csv', 'wb') as csvfile:
	#currentwriter = csv.writer(csvfile, delimiter=',', quotechar='|', quoting=csv.QUOTE_MINIMAL)

number = str(sys.argv[1])
filename = './data/measurements' + number + '.csv'
csv = open(filename, 'w+')
print('Measuring current...')
for x in range(0, 1000):
	average = 0.0
	for n in range(1, 100):
		# Read the difference between channel 0 and 1 (i.e. channel 0 minus channel 1).
		value = -adc.read_adc_difference(0, gain=GAIN, data_rate=DATARATE)
		value = value/4096.0*256/16/0.04*50
 		average = average*(n-1)/n + value*1.0/n
		# Pause
		time.sleep(0.001)

	#print(average)
	row = str(time.clock()) + ',' + str(round(average,2)) + '\n'
	#currentwriter.writerow([time.clock(), round(average, 2)])
	csv.write(row)
print('Measurements complete')
