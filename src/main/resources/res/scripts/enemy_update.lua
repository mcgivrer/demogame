local target = {}
target = objects:get("player")
if target:getX() > o:getX() or target:getX() < o:getX() :
    o:setDx( o:getDx()* -1)
end
return o
