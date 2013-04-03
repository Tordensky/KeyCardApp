from django.http import HttpResponse
from cards.models import Card, CardUser
from django.contrib.auth.models import User
import json

from django.contrib.auth.decorators import login_required

#@login_required
def index(request):
    print "GETTING CARDS"
    print request
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
            cardInfoDict = {'id':card.pk, 'name':card.name, 'value': card.value, 'cardIcon': card.cardIcon, 'exp_date': cardUserObject.expiry_date.isoformat() , 'role': cardUserObject.role}
            cardList.append(cardInfoDict)
    
        
    returnMsg = {'cards': cardList}
    returnMsg = json.dumps(returnMsg)
    print returnMsg
    return HttpResponse(returnMsg, content_type='application/json')

#@login_required
def getCard(request, card_id):
    print request.method
    print 'getting card request'
    if request.user.is_authenticated():
        if request.method == 'GET':
    #        try:
            card = Card.objects.filter(users__id = request.user.id)
            print card
            c = card.filter(pk = card_id)
            print c
    #        except Exception as e:
    #            return HttpResponse(status=500), e   
        
        
            return HttpResponse(c)
        elif request.method == 'DELETE':
            
            #cardToBeDeleted = Card.objects.get(pk = card_id)
            
            cardToBeDeleted = Card.objects.filter(pk = card_id)
            
            cardToBeDeleted.filter(users__id = request.user.id)
            #cardToBeDeleted.objects.filter(cardToBeDeleted.pk == card_id)
            if cardToBeDeleted.exists():
                for card in cardToBeDeleted:
                    cardUserObjects = card.carduser_set.all()
                    for cardUserObject in cardUserObjects:
                        role = cardUserObject.role
                        
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
                    #delete only your relation to card
                    
                #delete card and return OK
                return HttpResponse(status=200)
            else:
                #do noting and return 403
                return HttpResponse(status=403)



def createCard(request):
    print request.user.id,  "is trying to Create card"
    if request.user.is_authenticated():
        if request.method == 'POST':
            print 'GETTING POST'
     
            try:
                dicts = json.loads(request.body)
                print dicts
                name = dicts['name']
                value = dicts['value']
                cardIcon = dicts['cardIcon']
                exp_date = dicts['exp_date']
                
                print exp_date, "exp date!!"
                print name , "name!!!"
                print cardIcon, "cardfICON!!!!"
                print value, "VALUE"
                card = Card(name = name, value = value, cardIcon = cardIcon)
                card.save()
                print "test 1"
                cardUser = CardUser(user = request.user, card = card, expiry_date = exp_date)
                print "test2"
                cardUser.save()
            except Exception as e:
                print request.body
                #print e
                return HttpResponse(status=500)    
            return HttpResponse(card.id)
    return HttpResponse(status=400)
        
        
def deleteCard(request):
    print request.body        
        
        
        


def results(request, card_id):
    return HttpResponse("You're looking at the results of card %s." % card_id)

def vote(request, card_id):
    return HttpResponse("You're voting on card %s." % card_id)