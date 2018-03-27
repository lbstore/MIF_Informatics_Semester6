from Graphs import Link, DependencyDAG, Algorithms
from lib import readListFromFile
import sys


def simpleRun1(links:list):

    gr = DependencyDAG()
    for link in links:
        gr.addLink(link)
    order = Algorithms.topologicalSortKahn(gr)
    res = Algorithms.criticalPath(gr, order)
    size = res[1][res[2]]
    path = Algorithms.iterateMapKeys(res[0], res[2], None)
    path.reverse()
    print(size)
    print(path)


def simpleRun2(links:list):

    gr = DependencyDAG()
    for link in links:
        gr.addLink(link)
    # order = Algorithms.topologicalSortKahn(gr)
    res = Algorithms.criticalPathBackpropagation(gr)
    size = res[1][res[2]]
    path = Algorithms.iterateMapKeys(res[0], res[2], None)
    # path.reverse()
    print(size)
    print(path)

def getLinks(file:str) -> list:
    fileList = readListFromFile(file)
    links = list()
    for line in fileList:
        a = line.split(" ")
        links.append(Link(int(a[0]),int(a[1]),float(a[2])))
    return links


if __name__ == '__main__':
    try:
        debug = False
        if not len(sys.argv)==2:
            print("Specify only file")
        else:
            #print(sys.argv)
            simpleRun1(getLinks(sys.argv[1]))
    except Exception as e:
        print(e)
