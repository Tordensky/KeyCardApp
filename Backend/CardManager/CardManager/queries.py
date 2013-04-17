
from cards.models import Card, CardUser
from django.contrib.auth.models import User

def getUsersCards(request):
    try:
        cards = Card.objects.filter(users__id = request.user.id)
    except Exception as e:
        print e     