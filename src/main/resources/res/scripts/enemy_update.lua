local m = {
} 
function init(game,world)
  m.world = world;
end

function update(game, world, object, context)
  if(object:getTileCollision():getId()=="[" or object:getTileCollision():getId()=="]") then
    object::getSpeed():setX( object:getSpeed():getX() * -1);
  end
  return object
end
