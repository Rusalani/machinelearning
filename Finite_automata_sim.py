from pyeda.inter import *
import time
class node:
    
  
    def __init__(self,name,value):
        self.name=name
        self.neighbors=[]
        for x in range(0,32):
            if((value+3)%32 is x%32) or((value+8)%32 is x%32):
                temp = "{0:b}".format(x)
                while(len(temp) != 5):
                    temp = "0" + temp
                self.neighbors.append(temp)
                
def compare(one,two):
    for c in range(len(one)):
        if one[c] != two[c]:
            return False
    return True
def clean2(dest):
    dest = dest.replace("a","")
    dest = dest.replace("b","")
    dest = dest.replace("c","")
    dest = dest.replace("d","")
    dest = dest.replace("e","")
    dest = dest.replace(":","")
    dest = dest.replace("[","")
    dest = dest.replace("]","")
    dest = dest.replace("{","")
    dest = dest.replace("}","|")
    dest = dest.replace(" ","")
    dest = dest.replace(",","")
    dest = dest[:len(dest)-1]
    return dest
def clean(booleanString):
    booleanString = booleanString.replace("&","")
    booleanString = booleanString.replace("~a","0")
    booleanString = booleanString.replace("~b","0")
    booleanString = booleanString.replace("~c","0")
    booleanString = booleanString.replace("~d","0")
    booleanString = booleanString.replace("~e","0")
    booleanString = booleanString.replace("a","1")
    booleanString = booleanString.replace("b","1")
    booleanString = booleanString.replace("c","1")
    booleanString = booleanString.replace("d","1")
    booleanString = booleanString.replace("e","1")
    booleanString = booleanString[:len(booleanString)-1]
    
        
    return booleanString
def convert(s):
    temp =""
    
    if s[0] == "0":
        temp +="~a&"
    else:
        temp +="a&"
    if s[1] == "0":
        temp +="~b&"
    else:
        temp +="b&"
    if s[2] == "0":
        temp +="~c&"
    else:
        temp +="c&"
    if s[3] == "0":
        temp +="~d&"
    else:
        temp +="d&"
    if s[4] == "0":
        temp +="~e"
    else:
        temp +="e"
    return temp



prime = ["00011","00101","00111","01011","01101","10001","10011","10111","11101","11111"]
even = ["00000","00010","00100","00110","01000","01010","01100","01000","10000","10010","10100","10110","11000","11010","11100","11110"]
R=[]
RR=[]
EVEN = []
PRIME = []
RR2=[]
RR2star = []
PE = []


for x in range(0,32):
    temp = "{0:b}".format(x)
    while(len(temp) != 5):
        temp = "0" + temp
    R.append(node(temp,x))
    
for x in R:
    temp=""
    for y in x.neighbors:
        temp += convert(y) + "|"
    temp = temp[:len(temp)-1]
    f=expr(temp)
    f=expr2bdd(f)
    RR.append(f)
for x in prime:
    f=expr(convert(x))
    f=expr2bdd(f)
    PRIME.append(f)
for x in even:
    f=expr(convert(x))
    f=expr2bdd(f)
    EVEN.append(f)

for x in RR:
    temp = str(bdd2expr(x)).split("And")
    if("Or" in temp[0]):
        temp = temp[1:]
    listOfDest = []
    booleanString=""
    for y in temp:
        y=y.replace("(","")
        y=y.replace(")","")
        y=y.replace(",","")
        y=y.replace(" ","")
        booleanString+= y + "|"
        
    toVist = clean(booleanString).split("|")
    dest =""
    
    for location in toVist:
        temp = RR[int(location,2)]
        dest+=(str(list(temp.satisfy_all())))
    destList = clean2(dest).split("|")
    formula = ""
    for sequence in destList:
        formula+= "("+convert(sequence) + ")|"
    formula = formula[:len(formula)-1]
    f=expr(formula)
    f=expr2bdd(f)
    RR2.append(f)


RR2startemp = RR2[:]
for yyy in range(0,32):
    RR2star=[]
    #print(list(RR2startemp[0].satisfy_all()))
    for x in range(len(RR2)):
        temp = str(bdd2expr(RR2startemp[x])).split("And")
        if("Or" in temp[0]):
            temp = temp[1:]
        listOfDest = []
        booleanString=""
        for y in temp:
            y=y.replace("(","")
            y=y.replace(")","")
            y=y.replace(",","")
            y=y.replace(" ","")
            booleanString+= y + "|"
        #print(booleanString)
        toVist = clean(booleanString).split("|")
        dest =""
        #print(toVist)
        for location in toVist:
            
            temp = RR2[int(location,2)]
            #print(int(location,2))
            dest+=(str(list(temp.satisfy_all())))
            #print((str(list(temp.satisfy_all()))))
        #print(destList)
        destList = clean2(dest).split("|")
        #print(destList)
        formula = ""
        
        for sequence in destList:
            formula+= "("+convert(sequence) + ")|"
        #print(formula)
        temp3 = str(bdd2expr(RR2[x])).split("And")
        if("Or" in temp3[0]):
            temp3 = temp3[1:]
        
        booleanString="|".join(temp3)
        booleanString = booleanString.replace("),",")")
        booleanString = booleanString.replace(" ","")
        booleanString = booleanString.replace(",","&")
        booleanString = booleanString[:len(booleanString)-1]
            
        #print(booleanString)
        formula += booleanString
    
        #print(formula)
        
        f=expr(formula)
        #print(f)
        f=expr2bdd(f)
        RR2star.append(f)
        #print(len(RR2star))
    
    RR2startemp = RR2star[:]
   # print(list(RR2startemp[0].satisfy_all()))
    

                   
for x in range(len(prime)):
    primeNum = int(prime[x],2)
    primeBDD = PRIME[x]
    u=RR2star[primeNum]
    
    Uexp = str(bdd2expr(u)).split("And")
    if("Or" in Uexp[0]):
        Uexp = Uexp[1:]
    booleanString=""
    for y in Uexp:
        y=y.replace("(","")
        y=y.replace(")","")
        y=y.replace(",","")
        y=y.replace(" ","")
        booleanString+= y + "|"
    toVist = clean(booleanString).split("|")
    #print(toVist)
    localDest= "Or("
    for z in toVist:
        for y in range(len(even)):
            
            evenEXP = str(bdd2expr(EVEN[y]))
            evenEXP=evenEXP.replace("And","")
            evenEXP=evenEXP.replace(",","")
            evenEXP=evenEXP.replace("(","")
            evenEXP=evenEXP.replace(" ","")
            evenEXP = clean(evenEXP)
            if(compare(z,evenEXP)):
                #print(str(primeNum) + "->"+ str(int(even[y],2)))
                localDest+= str(bdd2expr(EVEN[y]))+", "
    localDest = localDest[:len(localDest)-2]            
    localDest +=")"
    
    PE.append(expr2bdd(expr(localDest)))

for x in PE:
    
    temp = str(bdd2expr(x)).split("And")
    if("Or" in temp[0]):
        temp = temp[1:]
    listOfDest = []
    booleanString=""
    for y in temp:
        y=y.replace("(","")
        y=y.replace(")","")
        y=y.replace(",","")
        y=y.replace(" ","")
        booleanString+= y + "|"
    toVist = clean(booleanString).split("|")
    for z in toVist:
        localEven = False
        for zz in even:
            if (compare(z,zz)):
                localEven = True

        if not localEven:
            print("False")
            break
print("True")


                
                      
    
