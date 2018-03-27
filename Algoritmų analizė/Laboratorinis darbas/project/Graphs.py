import copy

import math
import random
import timeit
from collections import deque

from lib import *


class Node:
    def __init__(self, ID:int):
        self.ID = ID
        self.linksTo = set()
        self.linkedFrom = set()

    def __str__(self):
        string = str(self.ID)
        links = "{}"
        linkedfrom = "{}"
        if len(self.linksTo) >0:
            links = str(self.linksTo)
        if len(self.linkedFrom) > 0:
            linkedfrom = str(self.linkedFrom)
        string += " -> " +links +" : "+linkedfrom
        return string

class Link:
    def __init__(self,nodeFrom:int,nodeTo:int,weight:float):
        self.nodeFrom = nodeFrom
        self.nodeTo = nodeTo
        self.weight = weight

    def key(self) -> int:
        return Link.hashMe(self.nodeFrom,self.nodeTo)

    @staticmethod
    def hashMe(nodeFrom:int, nodeTo:int) -> int:
        # Cantor pairing function
        # k = int(1/2 * (nodeFrom + nodeTo) * (nodeFrom + nodeTo +1) + nodeTo)
        k = hash((nodeFrom,nodeTo))
        return k

    def __str__(self):
        return str(self.nodeFrom) +" -"+str(self.weight)+"-> " + str(self.nodeTo)

class Orgraph:

    def __init__(self):
        self.nodes = Map()
        self.links = Map()

    def addLink(self,link:Link):
        n1 = self.__createNodeIfAbsent(link.nodeFrom)
        n2 = self.__createNodeIfAbsent(link.nodeTo)
        n1.linksTo.add(n2.ID)
        n2.linkedFrom.add(n1.ID)
        if self.links.containsKey(link.key()):
            print("Hash collision")
        self.links.put(link.key(),link)

    def linkNodes(self, nodeFrom:int, nodeTo:int, weight:float):
        self.addLink(Link(nodeFrom,nodeTo,weight))

    def add2wayLink(self,link:Link):
        self.linkNodes(link.nodeFrom, link.nodeTo, link.weight)
        self.linkNodes(link.nodeTo, link.nodeFrom, link.weight)

    def removeLink(self,nodeFrom:int,nodeTo:int):
        if self.nodes.containsKey(nodeFrom):
            self.__removeConnectionFromNode(self.nodes.get(nodeFrom),nodeTo)

    def remove2wayLink(self,link:Link):
        self.removeLink(link.nodeFrom,link.nodeTo)
        self.removeLink(link.nodeTo,link.nodeFrom)

    def removeNode(self,node:int):
        if self.nodes.containsKey(node):
            removeMe = self.nodes.get(node)
            linksTo = list(removeMe.linksTo)
            for n in linksTo:
                self.remove2wayLink(Link(n,node,0))
            self.nodes.removeByKey(node)

    def linkExists(self,nodeFrom:int,nodeTo:int):
        return self.links.containsKey(Link.hashMe(nodeFrom,nodeTo))

    def weight(self,nodeFrom:int,nodeTo:int):
        if self.linkExists(nodeFrom,nodeTo):
            return self.links.get(Link.hashMe(nodeFrom,nodeTo)).weight
        else:
            return None

    def getNode(self,nodeId:int) -> Node:
        if self.nodes.containsKey(nodeId):
            return self.nodes.get(nodeId)
        else:
            return Node(-1)

    def nodeIsLeaf(self,node:int) -> bool:
        isLeaf = False
        if self.nodes.containsKey(node):
            isLeaf = (0 == len(self.getNode(node).linksTo))
        return isLeaf

    def __removeConnectionFromNode(self,nodeFrom:Node, linkTo:int):

        nodeFrom.linksTo.discard(linkTo)
        if self.nodes.containsKey(linkTo):
            node = self.nodes.get(linkTo)
            node.linkedFrom.discard(nodeFrom.ID)
            self.links.removeByKey(Link.hashMe(nodeFrom.ID, node.ID))

    def __createNodeIfAbsent(self,node:int) -> Node:
        if not self.nodes.containsKey(node):
            newNode = Node(node)
            self.nodes.put(newNode.ID, newNode)
            return newNode
        else:
            return self.nodes.get(node)

    def toStringLinks(self):
        string = ""
        for link in self.links.values():
            string += link.__str__()+"\n"
        return string

    def toStringNodes(self):
        string = ""
        for node in self.nodes.values():
            string += node.__str__()+"\n"
        return string

    @classmethod
    def generateUndirected(cls, tallness: tuple, wideness: tuple, generateEdgeProbabilty):
        graph = Orgraph()
        nodes = 0
        ranks = tallness[0] + (random.randint(0, tallness[1] - tallness[0]))

        for i in range(0, ranks):
            newNodes = (random.randint(0, wideness[1] - wideness[0])) + wideness[0]
            for j in range(0, nodes):
                for k in range(0, newNodes):
                    if random.random() < generateEdgeProbabilty:
                        graph.add2wayLink(Link(j, k + nodes, 1))
            nodes += newNodes
        return graph

    @classmethod
    def fromMatrix(cls, matrix):
        graph = Orgraph()
        height = len(matrix)
        width = len(matrix[0])
        for i in range(0,height):
            for j in range(0,width):
                weigth = matrix[i][j]
                if weigth is not None:
                    graph.addLink(Link(i,j,weigth))
        return graph

class DAG(Orgraph):

    def __init__(self):
        super().__init__()

    def addLinkIfNoCycles(self, link:Link) -> bool:
        self.addLink(link)
        if Algorithms.cycleDetectionBFS(self,link.nodeTo):
            self.removeLink(link.nodeFrom,link.nodeTo)
            return False
        else:
            return True

    @classmethod
    def generateDAG(cls, tallness:tuple, wideness:tuple, generateEdgeProbabilty, maxWeight, graph:Orgraph = None):
        if graph is None or graph.nodes.__len__()==0:
            graph = DAG()
            graph.nodes.put(0, Node(0))
        nodes = 0
        ranks = tallness[0] + (random.randint(0, tallness[1] - tallness[0]))

        for i in range(0, ranks):
            newNodes = (random.randint(0, wideness[1] - wideness[0])) + wideness[0]
            for j in range(0, nodes):
                for k in range(0, newNodes):
                    if random.random() < generateEdgeProbabilty:
                        graph.addLink(Link(j, k + nodes, random.randint(1, maxWeight)))
            nodes += newNodes
        # print(Algorithms.containsCycle(graph))
        return graph

    @classmethod
    def generateSemiRandomDAG(cls, linkCount:int, maxWeight, nodeCount=None,graph:Orgraph = None):
        if graph is None or graph.nodes.__len__()==0:
            graph = DAG()
            graph.nodes.put(0, Node(0))
        def get2nodes(maxLen:int):
            nF, nT = 0,0
            while not nF < nT:
                nF = random.randint(0, maxLen)
                nT = random.randint(0, maxLen)
            return nF, nT
        if nodeCount is not None:
            while len(graph.nodes) < nodeCount:
                w = random.randint(1, maxWeight)
                nodeFrom = random.randint(0, len(graph.nodes)-1)
                nodeTo = len(graph.nodes)
                graph.addLink(Link(nodeFrom,nodeTo,w))

            while len(graph.nodes) > nodeCount:
                graph.removeNode(len(graph.nodes)-1)

        while len(graph.links) < linkCount:
            nodeFrom, nodeTo = get2nodes(len(graph.nodes))
            if not graph.linkExists(nodeFrom,nodeTo):
                w = random.randint(1, maxWeight)
                graph.addLink(Link(nodeFrom, nodeTo, w))

        # print(Algorithms.containsCycle(graph))
        return graph


    @classmethod
    def generateRandomDAG(cls, linkCount: int, maxWeight, nodeCount=None,graph:Orgraph = None):
        if graph is None or graph.nodes.__len__()==0:
            graph = DAG()
            graph.nodes.put(0, Node(0))
        triedLinks = dict()
        badTries = 0
        goodTries = 0


        def get2nodes(maxLen: int):
            nF, nT = 0, 0
            while nF == nT:
                nF = random.randint(0, maxLen)
                nT = random.randint(0, maxLen)
            return nF, nT

        if nodeCount is not None:
            while len(graph.nodes) < nodeCount:
                w = random.randint(1, maxWeight)
                nodeFrom = random.randint(0, len(graph.nodes) - 1)
                nodeTo = len(graph.nodes)
                link = Link(nodeFrom, nodeTo, w)
                reverseLink = Link(nodeTo,nodeFrom,w)
                if link.key() not in triedLinks:
                    goodTries += 1
                    graph.addLink(link)
                    triedLinks[reverseLink.key()] = reverseLink
                    triedLinks[link.key()] = link
            while len(graph.nodes) > nodeCount:
                graph.removeNode(len(graph.nodes) - 1)
        # print("Node addition end")
        w = random.randint(1, maxWeight)
        while len(graph.links) < linkCount:
            nodeFrom, nodeTo = get2nodes(len(graph.nodes))
            link = Link(nodeFrom, nodeTo, w)

            if link.key() not in triedLinks:
                if graph.addLinkIfNoCycles(link):
                    goodTries += 1
                    reverseLink = Link(nodeTo, nodeFrom, w)
                    triedLinks[reverseLink.key()] = reverseLink
                    w = random.randint(1, maxWeight)
                else:
                    badTries += 1
                triedLinks[link.key()] = link
            if Algorithms.cycleDetectionBFS(graph):
                print("Cycle at "+str(graph.links.__len__()))
                print(graph.toStringLinks())

        # print(goodTries,badTries,triedLinks.__len__())
        return graph

    @classmethod
    def generateRandomDAGrefined(cls, linkCount:int, maxWeight,batchSize=0.1, graph:Orgraph=None ):
        if graph is None or graph.nodes.__len__()==0:
            graph = DAG()
            graph.nodes.put(0, Node(0))
        while len(graph.links) < linkCount:
            possibleCandidates = set()
            possibleCandidates.update(graph.nodes)

            nodeFrom = random.randint(0, graph.nodes.__len__()-1)
            parentSet = Algorithms.getParentSet(graph,nodeFrom)
            parentSet.add(nodeFrom)
            parentSet.update(graph.getNode(nodeFrom).linksTo)
            possibleCandidates = possibleCandidates - parentSet
            possibleCandidates.add(graph.nodes.__len__())

            iterlimit = min(int(batchSize*possibleCandidates.__len__()),possibleCandidates.__len__())
            iterlimit = max(min(linkCount - graph.links.__len__(),iterlimit),1)
            pairs = random.sample(possibleCandidates, iterlimit)
            for pair in pairs:
                w = random.randint(1, maxWeight)
                graph.addLink(Link(nodeFrom, pair, w))

        return graph


    @classmethod
    def generateLeveledDAG(cls, height:int, width:int,generateEdgeProbabilty, maxWeight):
        graph = DAG()
        nodes = 0
        for i in range(0, height):
            nodes += width
            for j in range(nodes-width, nodes):
                for k in range(nodes, width+nodes):
                    if random.random() <= generateEdgeProbabilty:
                        graph.addLink(Link(j, k, random.randint(1, maxWeight)))
        return graph


    def getLevelMap(self) -> Map:
        levelMap = Map()
        levelCount = 0
        currentLayer = list()
        newLayer = list()
        nodes = set()
        nodes.update(self.nodes.values())
        for node in nodes:
            if len(node.linksTo) == 0:
                currentLayer.append(node.ID)

        while len(currentLayer) > 0:
            levelCount += 1
            for nodeID in currentLayer:
                levelMap.put(nodeID,levelCount)
                node = self.getNode(nodeID)
                newLayer.extend(node.linkedFrom)
            # currentLayer.clear()
            currentLayer = newLayer
            newLayer.clear()


        return levelMap

class DependencyDAG(DAG):
    def __init__(self):
        super().__init__()

    def getSupplementalLinks(self) -> list:

        taskWeights = Map()
        for node in self.nodes.values():
            price = 0
            if len(node.linksTo) >= 0:
                price = self.weight(node.ID,setPeek(node.linksTo))
            taskWeights.put(node.ID,price)

        res = list()

        def fetchLinks(nodeID:int) -> set:
            node = self.getNode(nodeID)
            fetchedNodes = set()
            fetchedNodes.update(node.linkedFrom)
            for dependantOn in node.linkedFrom:
                fetchedNodes.update(fetchLinks(dependantOn))
            for fetched in fetchedNodes:
                if not self.linkExists(fetched,nodeID):
                    link = Link(fetched, nodeID, taskWeights.get(fetched))
                    res.append(link)
            return fetchedNodes


        for node in self.nodes.values():
            if len(node.linksTo) == 0:
                fetchLinks(node.ID)

        return res

    def minimizeDependecies(self):
        levelMap = self.getLevelMap()
        linkedFrom = set()
        for node in self.nodes.values():
            nodeLevel = levelMap.get(node.ID)
            linkedFrom.update(node.linkedFrom)

            for dependencyID in linkedFrom:
                dependencyLevel = levelMap.get(dependencyID)
                if not nodeLevel + 1 == dependencyLevel:
                    self.removeLink(dependencyID,node.ID)
            linkedFrom.clear()

class Algorithms:

    @staticmethod
    def iterateMapKeys(m:dict,start:int, endKey:int=None) -> list:
        path = list()
        currentKey = start
        while not currentKey == endKey:
            path.append(currentKey)
            currentKey = m[currentKey]
        return path

    @staticmethod
    def getMapExtremum(m:dict, maximise=True) -> int:

        extremum = -float("inf")
        if not maximise:
            extremum = float("inf")
        ID = None
        for k in m:
            swap = False
            if m[k] is None:
                continue
            if maximise and (extremum < m[k]):
                swap = True
            elif not maximise and (extremum > m[k]):
                swap = True
            if swap:
                ID = k
                extremum = m[k]
        return ID

    @staticmethod
    def bellmanFord(graph:Orgraph, sourceID:int) -> Map:
        # rootNode = graph.getNode(sourceId)
        distanceMap = Map()
        for node in graph.nodes.values():
            distanceMap.put(node.ID,float("inf"))
        distanceMap.put(sourceID, 0)

        iterationMax = len(distanceMap)

        for i in range(0,iterationMax):
            changesMade = False
            for node in graph.nodes.values():
                # print(node)
                nodeValue = distanceMap.get(node.ID)
                # print(str(nodeValue) + " "+str(node.ID))
                if nodeValue >= 0:
                    for neighbour in node.linksTo:
                        distanceValue = distanceMap.get(neighbour)
                        newLinkValue = nodeValue + graph.weight(node.ID,neighbour)
                        # print(linkValue)
                        if distanceValue > newLinkValue:
                            distanceMap.put(neighbour, newLinkValue)
                            changesMade = True
            if not changesMade:
                # print("Iterations: "+str(i+1))
                break

        return distanceMap

    @staticmethod
    def topologicalSortKahn(graph: Orgraph) -> list:
        workList = deque()
        order = list()
        # calculate in-degree for each Node
        degreeMap = dict()
        for node in graph.nodes.values():
            degree = len(node.linkedFrom)
            id = node.ID
            degreeMap[id] = degree
            if degree == 0:
                workList.append(id)
        while len(workList) > 0:
            node = graph.getNode(workList.popleft())
            order.append(node.ID)
            for nextNodeID in node.linksTo:
                degreeMap[nextNodeID] -= 1
                if degreeMap[nextNodeID] == 0:
                    workList.append(nextNodeID)
        return order

    @staticmethod
    def topologicalSort(graph:Orgraph) -> list:
        nodesToVisit = list()
        order = list()

        # calculate in-degree for each Node
        degreeMap = dict()
        for nodeID in graph.nodes:
            n = graph.getNode(nodeID)
            degree = len(n.linkedFrom)
            degreeMap[nodeID] = degree
            if degree == 0:
                nodesToVisit.append(nodeID)

        def visit(nodeID:int):
            order.append(nodeID)
            node = graph.getNode(nodeID)
            for nextNodeID in node.linksTo:
                degreeMap[nextNodeID] -= 1
                if degreeMap[nextNodeID] == 0:
                    visit(nextNodeID)

        for n in nodesToVisit:
            visit(n)


        return order

    @staticmethod
    def criticalPath(graph: Orgraph, topologicalOrder: list) -> (dict, dict, int):
        """Must be Directed Acyclic Graph"""
        distanceMap = dict()  # node : longest distance to that node
        pathMap = dict()  # node : reached from
        negativeInf = float('-inf')
        for k in graph.nodes:
            distanceMap[k] = negativeInf
            pathMap[k] = None
        for nodeID in topologicalOrder:
            node = graph.getNode(nodeID)
            currentDist = distanceMap.get(nodeID)
            for neighbour in node.linksTo:
                if currentDist == negativeInf:
                    currentDist = 0
                neighbourDist = distanceMap.get(neighbour)
                tryNewDist = currentDist + graph.weight(nodeID, neighbour)
                if neighbourDist < tryNewDist:
                    distanceMap[neighbour] = tryNewDist
                    pathMap[neighbour] = nodeID
        startAt = Algorithms.getMapExtremum(distanceMap, maximise=True)
        return pathMap, distanceMap, startAt

    @staticmethod
    def criticalPathBackpropagation(graph: Orgraph) -> (dict, dict, int):
        """Must be Directed Acyclic Graph"""
        distanceMap = dict()  # node : longest distance from that node
        pathMap = dict()  # node : next node to go to
        outDegreeMap = dict()
        nodesToUpdate = list()
        for potentialEnd in graph.nodes.values():
            outDegree = len(potentialEnd.linksTo)
            potentialEndID = potentialEnd.ID
            outDegreeMap[potentialEndID] = outDegree
            if outDegree == 0:
                nodesToUpdate.append(potentialEndID)
                distanceMap[potentialEndID] = 0
            else:
                distanceMap[potentialEndID] = float('-inf')
            pathMap[potentialEndID] = None
        while len(nodesToUpdate) > 0:
            id = nodesToUpdate.pop()
            node = graph.getNode(id)
            for seenBy in node.linkedFrom:
                outDegreeMap[seenBy] -= 1
                if outDegreeMap[seenBy] == 0:
                    nodesToUpdate.append(seenBy)
                tryNewDist = distanceMap.get(id) + graph.weight(seenBy, id)
                if distanceMap[seenBy] < tryNewDist:
                    distanceMap[seenBy] = tryNewDist
                    pathMap[seenBy] = id
        startAt = Algorithms.getMapExtremum(distanceMap, maximise=True)
        return pathMap, distanceMap, startAt

    @staticmethod
    def travelingSalesmanExhaust(graph: Orgraph):
        size = len(graph.nodes)
        permutations = IndexGenerator.generateIndexList(size,size)
        longest = float('inf')
        longestPath = None
        for per in permutations:
            per.append(per[0])
            try:
                newLength = Algorithms.computePathLength(graph,per)
                if newLength < longest:
                    longest = newLength
                    longestPath = per
            except:
                pass
        return longestPath, longest

    @staticmethod
    def computePathLength(graph: Orgraph, p: list) -> int:
        if len(p) == 0:
            return int("-inf")
        length = 0
        path = p.copy()
        toNode = path.pop()
        while len(path) > 0:
            fromNode = path.pop()
            weight = graph.weight(fromNode, toNode)
            if weight is None:
                raise Exception("None path")
            length += weight
            toNode = fromNode
        return length

    @staticmethod
    def longestPathExhaust(graph:Orgraph):


        class PathWithLength:
            def __init__(self,path:list,length:int):
                self.path = path
                self.length = length
            def __str__(self):
                return str(self.length)+" : "+str(self.path)

        class MutableInteger:
            def __init__(self, i:int):
                self.me = i

        def propogate(nodeID: int, visited: set, currentPath: list,bestPath:PathWithLength,checked:MutableInteger):
            propogateList = list()
            currentPath.append(nodeID)
            visited.add(nodeID)
            currentNode = graph.getNode(nodeID)
            for link in currentNode.linksTo:
                if link not in visited:
                    propogateList.append(link)
            if len(propogateList) == 0:  # path end
                newLength = Algorithms.computePathLength(graph, currentPath)
                if newLength > bestPath.length:
                    bestPath.length = newLength
                    bestPath.path = currentPath
                    # print("New best path "+str(currentPath))
                checked.me += 1
                # if checked.me % 10000 == 0:
                #     print("Checked "+str(checked.me))

            else:
                for newNodeID in propogateList:
                    newVisited = visited.copy()
                    newPath = currentPath.copy()
                    propogate(newNodeID,newVisited,newPath,bestPath,checked)


        checked = MutableInteger(0)
        longestPath = PathWithLength(list(), 0)
        for node in graph.nodes:
            startPath = list()
            startVisited = set()
            propogate(node,startVisited.copy(),startPath.copy(),longestPath,checked)

        print(checked.me)
        return longestPath

    @staticmethod
    def longestPathExhaustStack(graph: Orgraph):
        class PathWithLength:
            def __init__(self, path: list, length: int):
                self.path = path
                self.length = length

            def __str__(self):
                return str(self.length) + " : " + str(self.path)

        class PropogatingNode:
            def __init__(self,nodeID:int, visited:set,path:list):
                self.visited = visited.copy()
                self.path = path.copy()
                self.ID = nodeID


        def spawnNewNodes(node:PropogatingNode) ->list:
            spawn = list()
            node.path.append(node.ID)
            node.visited.add(nodeID)
            currentNode = graph.getNode(node.ID)
            for link in currentNode.linksTo:
                if link not in node.visited:
                    spawn.append(PropogatingNode(link, node.visited, node.path))
            return spawn

        checked = 0
        longestPath = PathWithLength(list(), 0)
        switchList = SwitchList()
        for nodeID in graph.nodes:
            propNode = PropogatingNode(nodeID,set(),list())
            switchList.append(propNode)

        while True :
            if switchList.isEmpty():
                switchList.switch()

            rootNode = switchList.pop()
            newNodes = spawnNewNodes(rootNode)
            if len(newNodes) == 0: #check path length
                newLength = Algorithms.computePathLength(graph,rootNode.path)
                if newLength > longestPath.length:
                    longestPath.path = rootNode.path
                    longestPath.length = newLength
                    # print("New best path " + str(rootNode.path))
                checked += 1
                # if checked % 10000 == 0:
                #     print("Checked " + str(checked))
            else:
                switchList.extend(newNodes)

            if switchList.bothEmpty():
                break


        print(checked)
        return longestPath

    @staticmethod
    def longestPathHeuristic(graph: Orgraph):

        class MergeSet:
            def __init__(self, nodeID):
                self.path = list()
                self.path.append(nodeID)
                self.used = False
            def getTop(self) -> Node:
                return self.path[0]

            def getBottom(self) -> Node:
                return self.path[-1]

        powerMap = Map()
        mergeList = list()
        newList = list()


        for node in graph.nodes.values():
            power = len(node.linksTo)
            powerMap.put(node.ID,power)
            mergeList.append(MergeSet(node))

        changed = True
        while changed:
            changed = False
            containsUnused = True
            while containsUnused:
                S1 = mergeList.pop()
                S1.used = True
                potentialMerge = list()
                for S2 in mergeList:
                    if S2.used: #not this iteration
                        newList.append(S2)
                    else:
                        potentialMerge.append(S2)
                mergeList.clear()
                pairs = list()
                for sets in potentialMerge:
                    if sets.getTop() in S1.getTop().linkedTo:
                        pass




                pass


    @staticmethod
    def depthFirstListNodes(startingNode:int, graph:Orgraph):
        visited = set()
        order = list()

        def visit(nodeID:int,root:int):

            # if nodeID not in visited:
                print("From " + str(root+1))
                print("Visit " + str(nodeID+1))
                visited.add(nodeID)
                order.append(nodeID)
                node = graph.getNode(nodeID)
                nodes = list(node.linksTo)
                for nextNodeID in nodes:
                    if nextNodeID not in visited:
                        visit(nextNodeID,nodeID)
                # print("Come back to "+str(nodeID))

        visit(startingNode,-1)

        return order

    @staticmethod
    def breadthFirstListNodes(startingNode: int, graph: Orgraph):
        visited = set()
        order = list()
        visitNextIteration = list()
        visitNextIteration.append(startingNode)
        newList = list()

        def visit(nodeID: int,appendToMe:list):
            if nodeID not in visited:
                visited.add(nodeID)
                order.append(nodeID)
                node = graph.getNode(nodeID)
                for nextNodeID in node.linksTo:
                    if nextNodeID not in (visited or appendToMe):
                        appendToMe.append(nextNodeID)

        while True:
            l = [e+1 for e in visitNextIteration]
            print("Next iteration:" + str(l))
            for id in visitNextIteration:
                visit(id,newList)
            visitNextIteration.clear()
            visitNextIteration.extend(newList)
            newList.clear()

            # order.append(-111)
            if len(visitNextIteration) == 0:
                break


        return order

    @staticmethod
    def cycleDetectionBFS(graph: Orgraph, startNode=None):
        visited = set()
        visitNextIteration = set()
        if startNode is None:
            for n in graph.nodes:
                if Algorithms.cycleDetectionBFS(graph,n):
                    return True
            return False
        else:
            visitNextIteration.add(startNode)
        newList = set()

        #propagate
        while visitNextIteration:
            nodeID = visitNextIteration.pop()
            if nodeID in visited:
                continue
            visited.add(nodeID)
            node = graph.getNode(nodeID)
            for link in node.linksTo:
                if link == startNode:
                    return True
                else:
                    newList.add(link)
            if len(visitNextIteration) == 0:
                newList,visitNextIteration = visitNextIteration,newList
        return False

    @staticmethod
    def cyclic(graph:Orgraph,startingNode:int=None):

        path = set()
        visited = set()

        def visit(vertex):
            if vertex in visited:
                return False
            visited.add(vertex)
            path.add(vertex)
            for neighbour in graph.getNode(vertex).linksTo:
                if neighbour in path or visit(neighbour):
                    return True
            path.remove(vertex)
            return False
        if startingNode is None:
            return any(visit(v) for v in graph.nodes)
        else:
            return visit(startingNode)

    @staticmethod
    def getParentSet(graph:Orgraph, node:int) -> set:
        newSet = set()
        visitNextIteration = set()
        parentSet = set()
        visitNextIteration.add(node)
        while visitNextIteration:
            newSet.clear()
            for up in visitNextIteration:
                if up in parentSet:
                    continue
                parentSet.add(up)
                parent = graph.getNode(up)
                newSet.update(parent.linkedFrom)
            visitNextIteration.clear()
            visitNextIteration.update(newSet)
            newSet.clear()
        return parentSet


























