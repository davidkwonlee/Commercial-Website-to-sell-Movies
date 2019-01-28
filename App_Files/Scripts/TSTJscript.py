import os
def run_script():
    read_file = open("totaltimes.txt", 'r')
    TS_total = 0
    TJ_total = 0
    pointer = 0
    
    for nums in read_file:
        split_line = nums.split(":")
        if split_line != " ":
            TS_total += split_line[0]
            TJ_total += split_line[1]
            pointer += 1
       else:
            return
    read_file.close()

path = str(os.path.dirname(os.path.abspath(__file__)))

fw = path + "/TS_TJavg.txt"
write_file = open(fw, 'w')
write_file.write("TS: {}.\n".format(str((float(TS_total)/lineNum)/1000000)))
write_file.write("TJ: {}.\n".format(str((float(TJ_total)/lineNum)/1000000)))

write_file.close()



if __name__ == "__main__":
    run_script()

