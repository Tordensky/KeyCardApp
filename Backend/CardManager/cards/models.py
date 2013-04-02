from django.db import models
from django.contrib.auth.models import User

class Card(models.Model):
    name = models.CharField(max_length = 25)
    value = models.CharField(max_length=200)
    cardIcon = models.IntegerField(default=0)
    users = models.ManyToManyField(User, through='CardUser')
    
    
    def __unicode__(self):
        return self.name
    
    
class CardUser(models.Model):
    user = models.ForeignKey(User)
    card = models.ForeignKey(Card)
    role = models.IntegerField(default=0)
    expiry_date = models.DateTimeField()
    
    
    