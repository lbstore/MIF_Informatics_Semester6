# coding=utf-8
import sys
from collections import deque

import math


def utfPrint(text):
    print(text, "utf-8")

def formatListToString(array):
    string = ""
    for x in array:
        if x is None:
            x = " "

        string += str(x)+u","

    return "["+string[:-1] + "]"

def stringReplace(source:str,target:str,replacement="")->str:
    if target not in source:
        return source
    try:
        index = source.index(target)
        new = source[:index] + replacement + source[len(target)+index:]
    except ValueError:
        return source
    return new

def printListToFile(filePath:str,array:list):
    file = open(filePath,'w')
    for s in array:
        if "\n" not in s:
            s+="\n"
        file.write(s)
    file.flush()
    file.close()

def readListFromFile(filePath:str)->list:
    file = open(filePath,'r')
    array = list()
    for line in file.readlines():
        if line.__len__()>1:
            array.append(stringReplace(line,'\n'))
    return array


class ArrayList(list):

    def __init__(self, data=None, nullValue=" "):
        super().__init__()
        self.nullValue = nullValue
        if data is not None:
            self.extend(data)

    def __fitsInRange(self, index:int):
        if self.__len__() <= index:
            return False
        else:
            return True

    def appendToSize(self, requiredSize):
        size = self.__len__()
        for i in range(size,requiredSize):
            self.append(self.nullValue)

    def indexOf(self, value):
        i = -1
        for val in self:
            i+=1
            if value == val:
                return i
        return -1

    def lastIndexOf(self, value):
        i = self.__len__()
        while i>=0:
            i -= 1
            if value == self[i]:
                return i
        return -1

    def insert(self, index:int, value):
        self.appendToSize(index)
        super().insert(index, value)

    def set(self,index:int, value):
        self.appendToSize(index+1)
        self[index] = value

    def populateFromString(self, text):
        for i in text:
            self.append(i)

    def get(self,index:int):
        return self[index]

    def extendAt(self, iterable, startAt=None):
        if startAt is None:
            super().extend(iterable)
        else:
            self[startAt:startAt] = iterable

    def getLast(self):
        if self.__len__()==0:
            return None
        return self[self.__len__()-1]

    def size(self):
        return self.__len__()

    def getItemsInReverseOrder(self):
        index = self.__len__()
        items = []
        if index == 0:
            return items
        while index>0:
            index -= 1
            items.append(self[index])

        return items

    def __str__(self):
        string = "["
        for st in self:
            string+=str(st)+","
        string = string[:-1]+"]"
        return string

class IndexGenerator:

    @staticmethod
    def __go1(indexArrayList: ArrayList, current: ArrayList, left: ArrayList, i:int, maxLength:int):
        length = current.__len__()

        if not length < maxLength:
            indexArrayList.append(current)
            # Debug.print("add:"+current.__str__())
            return
        else:
            current.append(left.pop(i))
            length = left.__len__()
            # Debug.print("Cur:" + current.__str__() + " Left:" + left.__str__() + " len =" + str(length), end="")
            # Debug.print(" index:" + str(i))
            a = 0
            if a<length:
                while a < length:
                    IndexGenerator.__go1(indexArrayList,current.copy(), left.copy(), a, maxLength)
                    a += 1
            else:
                indexArrayList.append(current)
                # Debug.print("add:" + current.__str__())
                return

    @staticmethod
    def __go(indexArrayList: dict, current: ArrayList, left: ArrayList, i:int, maxLength:int):
        length = current.__len__()

        if length >= maxLength:
            indexArrayList.setdefault(current.__str__(),current)
            # Debug.print("add:"+current.__str__())
            return
        else:
            current.append(left.pop(i))
            length = left.__len__()
            # Debug.print("Cur:" + current.__str__() + " Left:" + left.__str__() + " len =" + str(length), end="")
            # Debug.print(" index:" + str(i))
            a = 0
            if a<length:
                while a < length:
                    IndexGenerator.__go(indexArrayList,current.copy(), left.copy(), a, maxLength)
                    a += 1
            else:
                indexArrayList.setdefault(current.__str__(), current)
                # Debug.print("add:" + current.__str__())
                return

    @staticmethod
    def generateIndexList(size:int,maxLength:int)->list:
        maxLength = min(size,maxLength)
        # print("MaxLen:"+str(maxLength))
        indexArrayList = dict()
        indexArray = ArrayList()

        for i in range(0, size):
            indexArray.append(i)
        for i in range(0, size):
            newArray = ArrayList()
            IndexGenerator.__go(indexArrayList, newArray.copy(), indexArray.copy(), i, maxLength)
        array = ArrayList()
        for each in indexArrayList.values():
            array.append(each)
        array.sort()
        return array

    @staticmethod
    def arrayObjectCrossProduct(array:list,objects:list):
        newArray = ArrayList()
        for ar in array:
            for ob in objects:
                newObject = list()
                try:
                    newObject.extend(ar)
                except TypeError:
                    newObject.append(ar)
                try:
                    newObject.extend(ob)
                except TypeError:
                    newObject.append(ob)

                newArray.append(newObject)

        return newArray

    @staticmethod
    def generateAllPossibleIndexes(indexCount:int,maxLength:int)->list:
        finalArray = ArrayList()

        for i in range(0,indexCount):
            finalArray.append(i)
        array = finalArray.copy()

        for i in range(0,maxLength-1):
            finalArray = IndexGenerator.arrayObjectCrossProduct(finalArray,array)

        return finalArray

    class IndexArrayWithIncrements:
        def __init__(self,indexCount:int, maxLength:int,startingValue=0):
            self.maxValue = indexCount-1
            self.maxLength = maxLength
            self.array = ArrayList()
            for i in range(0,maxLength):
                self.array.append(0)
            self.increment(startingValue)

        def __bump(self):
            currentIndex = self.maxLength-1

            while not currentIndex<0:
                if self.array[currentIndex] < self.maxValue:
                    self.array[currentIndex]+=1
                    return
                else:
                    self.array[currentIndex] = 0
                currentIndex+=-1

        def increment(self, amount=1):
            for i in range(0,amount):
                self.__bump()

        def containsIndexPair(self):
            tempSet = set()
            for i in self.array:
                if i in tempSet:
                    return True
                else:
                    tempSet.add(i)
            return False

        def returnValueByAlphabet(self,alphabet:str):
            string=""
            for i in self.array:
                string+= alphabet[i]
            return string

        def getPossibleVariationCount(self):
            return math.pow(self.maxValue+1,self.maxLength)

class Table:
    def __init__(self, data=None):

        self.name = "Table"
        self.nullValue =" "
        self.lines = ArrayList(ArrayList(None,self.nullValue))
        if data is not None:
            self.lines.extend(data)

    def equalize(self):
        maxTableLen = 0
        for line in self.lines:
            if maxTableLen < line.__len__():
                maxTableLen = line.__len__()
        for line in self.lines:
            line.appendToSize(maxTableLen)

    def addLine(self, line=None):
        arrayList = ArrayList(line,self.nullValue)
        self.lines.append(arrayList)

    def remove(self, line, column):
        ob = self.lines[line].pop(column)
        return ob

    def get(self, line, column):
        ob = self.lines[line]
        return ob[column]

    def set(self, line: int, column: int, value):
        while line >= self.getLineCount():
            self.addLine()

        self.lines[line].set(column, value)

    def getLineCount(self):
        return self.lines.__len__()

    def getColumnCount(self):
        m = 0
        for line in self.lines:
            m = max(m,line.__len__())
        return m
    def getColumn(self,col)->list:
        column = ArrayList()
        for i in range(self.getLineCount()):
            column.append(self.get(i,col))
        return column

    def setLine(self,index:int,line:list):
        i = 0
        for value in line:
            self.set(index, i, value)
            i += 1

    def setColumn(self,index:int,column:list):
        i=0
        for value in column:
            self.set(i,index,value)
            i+=1

    def getFromTopLeft(self,index:int):
        i =0
        for line in self.lines:
            for value in line:
                if i == index:
                    return value
                i+=1

    def setFromTopLeft(self,index:int,value):
        i = 0
        for line in self.lines:
            for valIndex in range(0,line.__len__()):
                if i == index:
                    line[valIndex] = value
                    return
                i += 1

    def indexFromTopLeft(self,findMe):
        i = 0
        for line in self.lines:
            for value in line:
                if findMe == value:
                    return i
                i += 1
        return -1
    def indexFromTop(self,findMe):
        i = 0
        for line in self.lines:
            if line.indexOf(findMe) >= 0:
                return i
            i+=1
        return -1

    def indexFromLeft(self,findMe):
        for line in self.lines:
            indexOf = line.indexOf(findMe)
            if indexOf >= 0:
                return indexOf
        return -1

    def removeLine(self,index:int):
        del self.lines[index]

    def removeColumn(self,index:int):
        for line in self.lines:
            line.pop(index)

    def getCellCount(self):
        return self.getColumnCount()*self.getLineCount()

    def __copy__(self):
        table = Table()
        for i in range(0,self.getLineCount()):
            table.addLine(self.lines[i].copy())
        return table

    def __str__(self):
        string = self.name+u"\n"
        i = 0
        for line in self.lines:
            string += str(i)+":"+line.__str__()+u"\n"
            i+=1
        return string

    def printMe(self):
        print(self.name)
        for line in self.lines:
            print(line.__str__())

    def __rotateCW(self):
        table = Table()
        for i in range(0,self.getLineCount()):
            line = self.lines[i]
            table.setColumn(self.getLineCount()-i-1,line)
        self.lines = table.lines

    def __rotateCCW(self):
        table = Table()
        for i in range(0, self.getLineCount()):
            line = self.lines[i]
            line.reverse()
            table.setColumn(i, line)
        self.lines = table.lines

    def rotateClockwise(self, times=1):
        for i in range(0,times):
            self.__rotateCW()

    def rotateCounterClockwise(self, times=1):
        for i in range(0,times):
            self.__rotateCCW()

    def __flipVertical(self):
        for line in self.lines:
            line.reverse()

    def flip(self, VerticalAxis=True):
        if not VerticalAxis:
            self.rotateClockwise(2)
        self.__flipVertical()

    def applyNullValue(self,nullValue):
        for i in range(0,self.getCellCount()):
            if self.getFromTopLeft(i) == self.nullValue:
                self.setFromTopLeft(i,nullValue)
        self.nullValue = nullValue
        for line in self.lines:
            line.nullValue = nullValue

    def applyToEachElement(self,function):
        for i in range(0, self.getCellCount()):
            self.setFromTopLeft(i, function(self.getFromTopLeft(i)))

    @staticmethod
    def createTable(text, lineLength, linesAreColumns=False):
        table = Table()
        index = 0
        lineIndex = 0
        origline = ArrayList()
        for i in text:
            origline.append(i)
        line = ArrayList()
        size = origline.__len__()

        if not linesAreColumns:
            while index < size:
                if lineIndex >= lineLength:
                    table.addLine(line)
                    line = ArrayList()
                    lineIndex = 0
                line.set(lineIndex, origline[index])
                index += 1
                lineIndex += 1
            table.addLine(line)
        else:
            while table.getLineCount() < lineLength:
                table.addLine(ArrayList())
            currentIndex = 0
            while index < size:
                if lineIndex >= lineLength:
                    lineIndex = 0
                    currentIndex += 1
                table.set(lineIndex, currentIndex, origline[index])
                index += 1
                lineIndex += 1
        return table


class Map(dict):
    def __init__(self, **kwargs):
        super().__init__(**kwargs)

    def containsKey(self,key):
        return key in self

    def containsValue(self,val):
        return val in self.values()

    def put(self, key, value):
        self[key] = value

    def removeByKey(self, key):
        if key in self:
            return self.pop(key)
        return None

    def peek(self):
        for e in self.values():
            return e


class OrderedMap(Map):
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.keyOrder = list()

    def returnItemsInOrder(self):
        l = []
        for key in self.keyOrder:
            l.append(self.get(key))
        return l

    def put(self, key, value):
        if not self.containsKey(key):
            self.keyOrder.append(key)
        self[key] = value

    def removeByKey(self, key):
        if key in self:
            self.keyOrder.remove(key)
            return self.pop(key)
        return None

    def clear(self):
        super().clear()
        self.keyOrder.clear()

class SwitchList:
    def __init__(self):
        self.addingList = list()
        self.removingList = list()

    def pop(self):
        return self.removingList.pop()

    def append(self, value):
        self.addingList.append(value)

    def extend(self,values:iter):
        self.addingList.extend(values)

    def isEmpty(self):
        return len(self.removingList) == 0

    def bothEmpty(self):
        return len(self.addingList) + len(self.removingList) == 0

    def switch(self):
        temp = list()
        temp = self.addingList
        self.addingList = self.removingList
        self.removingList = temp

def eprint(*args, **kwargs):
    print(*args, file=sys.stderr, **kwargs)

def cmp_to_key(mycmp):
    'Convert a cmp= function into a key= function'
    class K(object):
        def __init__(self, obj, *args):
            self.obj = obj
        def __lt__(self, other):
            return mycmp(self.obj, other.obj) < 0
        def __gt__(self, other):
            return mycmp(self.obj, other.obj) > 0
        def __eq__(self, other):
            return mycmp(self.obj, other.obj) == 0
        def __le__(self, other):
            return mycmp(self.obj, other.obj) <= 0
        def __ge__(self, other):
            return mycmp(self.obj, other.obj) >= 0
        def __ne__(self, other):
            return mycmp(self.obj, other.obj) != 0
    return K

def convertToInt(b:list):
    sk = 0
    p = 0
    b.reverse()
    for i in b:
        sk+= (2**p) * i
        p+=1
    return sk


def convertToBits(s:int):
    pow = 0
    i = 0
    s+=1
    bits = list()
    while (s>pow):
        pow = 2**i
        i+=1
    # found max
    while(i>=0):
        pow = 2**i
        if(s>pow):
            s-=pow
            bits.append(1)
        else:
            bits.append(0)
        i-=1
    while bits.__len__()>1 and (bits[0]==0):
        bits.pop(0)

    return bits


def setPeek(s: set):
    for e in s:
        return e


