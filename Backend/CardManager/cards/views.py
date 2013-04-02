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
                    cards = Card.objects.all()
                except Exception as e:
                    return HttpResponse(status=500)
            else:
                try:
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
            cardInfoDict = {'id':card.pk, 'name':card.name, 'value': card.value, 'cardIcon': card.cardIcon, 'expiry_date': cardUserObject.expiry_date.isoformat() , 'role': cardUserObject.role}
        cardList.append(cardInfoDict)
    
        
    returnMsg = {'cards': cardList}
    returnMsg = json.dumps(returnMsg)
    
    return HttpResponse(returnMsg, content_type='application/json')

#@login_required
def getCard(request, card_id):
    print request
    print 'getting card request'
    if request.method == 'GET':
#        try:
        card = Card.objects.filter(users__id = request.user.id)
        print card
        c = card.filter(pk = card_id)
        print c
#        except Exception as e:
#            return HttpResponse(status=500), e   
    
    
    return HttpResponse(c)


#
#def createCard(request):
#    print request.user.id,  "is trying to Create card"
#    
#    if request.method == 'POST':
        
        
        


def results(request, card_id):
    return HttpResponse("You're looking at the results of card %s." % card_id)

def vote(request, card_id):
    return HttpResponse("You're voting on card %s." % card_id)