
from django.http import HttpResponse
from cards.models import Card, CardUser
from django.contrib.auth.models import User
from datetime import datetime
import json


def index(request):
    print "GETTING CARDS"
    #print request
    if request.user.is_authenticated():
        if request.method == 'GET':
            cards = ""
            if request.user.is_superuser:
                try:
                    print "is superuser, should give all cards"
                    cards = Card.objects.all()
                except Exception as e:
                    return HttpResponse(status=500)
            else:
                try:
                    print "not superuser, should only give his cards"
                    cards = Card.objects.filter(users__id = request.user.id)
                except Exception as e:
                    return HttpResponse(status=500), e
        elif request.method == 'POST':
            pass
    else:
        return HttpResponse(status=401)

    cardList = []   
    for card in cards:
        cardUserObjects = card.carduser_set.all()
        for cardUserObject in cardUserObjects: 
            cardInfoDict = {}
            if cardUserObject.user == request.user:
                print str(datetime.now())
                print cardUserObject.expiry_date.isoformat()
                print cardUserObject.expiry_date
                
                if cardUserObject.expiry_date <= datetime.now() and cardUserObject.expired == False:
                    changeExpired(cardUserObject)
                
                    
                cardInfoDict = {'id':card.pk, 'name':card.name, 'value': card.value, 'cardIcon': card.cardIcon, 'exp_date': cardUserObject.expiry_date.isoformat() , 'role': cardUserObject.role, 'expired':cardUserObject.expired}
                    
                if cardUserObjects.count() > 1:  
                    cardInfoDict['shared'] = True
                else:
                    cardInfoDict['shared'] = False
                cardList.append(cardInfoDict)
            continue
                   

                
        
        
    returnMsg = {'cards': cardList}
    returnMsg = json.dumps(returnMsg)
    print returnMsg
    return HttpResponse(returnMsg, content_type='application/json')




def changeExpired(cardUserObject):
    if cardUserObject.expired == True:
        cardUserObject.expired = False
    else:
        cardUserObject.expired = True
    cardUserObject.save()

def shareCard(request, card_id):
    if request.user.is_authenticated():
        if request.method == 'POST':
            try:
                cards = Card.objects.filter(users__id = request.user.id)
                cardToShare = cards.filter(pk = card_id)
            except Exception as e:
                print e
                return HttpResponse(status=500)
            if cardToShare.exists():
                dicts = json.loads(request.body)
                name = dicts['username']
                exp_date = dicts['exp_date']
                
                # user does not exist, cannot share with non existing user
                try:
                    userToShareKey = User.objects.get(username = name)
                except Exception as e:
                    return HttpResponse(status=404)

                # user is self, cannot share with yourself
                if request.user.username == name:
                    return HttpResponse(status=403)
                
                existingCardUser = Card.objects.filter(users__id = userToShareKey.pk, pk = card_id)

                # user already has this card
                if existingCardUser.exists():
                    return HttpResponse(status=403)
                
                # share the card
                for card in cardToShare:
                    cardUserObjects = card.carduser_set.all()
                    for cardUserObject in cardUserObjects:
                        role = cardUserObject.role
                        if role == 0:
                            try:
                                cardUser = CardUser(user = userToShareKey, card = card, expiry_date = exp_date, role=1)
                                cardUser.save()
                            except Exception as e:
                                print e
                                return HttpResponse(status=500)
                        else:
                            continue
                        
            return HttpResponse(status=200)
        return HttpResponse(status=400)
    else:
        return HttpResponse(status=401)

# URL : /cards/id
def getCard(request, card_id):
    if request.user.is_authenticated():
        if request.method == 'GET':
            response = handleGetCardRequest(request, card_id)
            return response
              
        elif request.method == 'DELETE':
            response = handleDeleteCardRequest(request, card_id) 
            return response        
    else:
        # user not authenticated return 401 Unauthorized
        return HttpResponse(status=401)


def handleGetCardRequest(request, card_id):
    try:
        card = Card.objects.filter(users__id = request.user.id)
        c = card.filter(pk = card_id)
        return HttpResponse(c)
    except Exception as e:
        return HttpResponse(status=500), e   
    
def handleDeleteCardRequest(request, card_id):
    try:
        cardToBeDeleted = Card.objects.filter(pk = card_id)            
        cardToBeDeleted.filter(users__id = request.user.id)
    except Exception as e:
        print e, "1"
        return HttpResponse(status=500) 
    
    if cardToBeDeleted.exists():
        for card in cardToBeDeleted:
            cardUserObjects = card.carduser_set.all()
            for cardUserObject in cardUserObjects:
                # get current users role
                if cardUserObject.user == request.user:  
                    role = cardUserObject.role
        # only role 0 can delete card completely        
        if role == 0:
            try:
                CardUser.objects.filter(card = card_id).delete()
                cardToBeDeleted.delete()
            except Exception as e:      
                print e, "DELETING FAILED, role was 0"
                return HttpResponse(status=500)  
            #delete card and all relations
            
        else:
            try:
                cardUserToBeDeleted = CardUser.objects.filter(card = card_id)
                cardUserToBeDeleted.filter(user = request.user).delete()                    
            except Exception as e:      
                print e, "DELETING FAILED, role was 1"
                return HttpResponse(status=500)                 
        return HttpResponse(status=200)
    else:
        #do noting and return 403 Forbidden
        return HttpResponse(status=403)
    
# URL /cards/new
def createCard(request):
    if request.user.is_authenticated():
        if request.method == 'POST': 
            try:
                formDict = json.loads(request.body, encoding = 'latin1')
            except ValueError as e:
                print e
                return HttpResponse(status=500)
            try:
                values = parseCreateCardFormData(formDict)             
                card = Card(name = values[0], value = values[1], cardIcon = values[2])
                card.save()
                cardUser = CardUser(user = request.user, card = card, expiry_date = values[3])
                cardUser.save()
            except Exception as e:
                print e
                return HttpResponse(status=500)    
            return HttpResponse(card.id)
        else:
            return HttpResponse(status=400)
    
    return HttpResponse(status=401)

def parseCreateCardFormData(formDict):
    keys = []
    try:
        name = formDict['name'].lower()
        value = formDict['value']
        cardIcon = formDict['cardIcon']
        exp_date = formDict['exp_date']
    except KeyError as e:
        print e
        return keys
    keys = [name, value, cardIcon, exp_date]
    return keys
    
    
def getUsersCards(request):
    try:
        cards = Card.objects.filter(users__id = request.user.id)
    except Exception as e:
        print e       
        
def isExpired(card):
    pass
    
    
