import smbus
import time
bus = smbus.SMBus(1)
address = 0x36
vref = 2.048

def adcConfig():
	setup = 0b11110010
	config = 0b00000011
	bus.write_byte_data(address, 0x01, setup)
	bus.write_byte_data(address, 0x01, config)

def readCurrent():
	current = 0.0
	current =  bus.read_word_data(address, 0x00) & 0x03FF
	return current

print('Configuring MAX11647 for current measurement.')
adcConfig()
print('Configuration complete.')
time.sleep(0.7)
print('Reading current...')
for x in range(0, 10):
	#current = (2.048*readCurrent())/1024/50/0.04
	currentA = bus.read_i2c_block_data(address, 0x00, 2)
	current = (currentA[0] & 0x3) * 256 + currentA[1]
	current = current*vref/1024/2
	print(current)
	print(format(currentA[0], 'b'))
	print(format(currentA[1], 'b'))
	#print(str(x) + ' ' + format(currentA[1], 'b'))
	time.sleep(0.20)
