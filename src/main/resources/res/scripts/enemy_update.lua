local m = {
} 
function init(game,world)
  m.world = world;
end

function update(game, world, object, context)
  local player = {}
  player = context:get("player")
  if( player:getPosision():getX() > object:getPosition():getX() or player:getPosition():getX() < object:getPosition():getX() ) then
      object::getSpeed():setX( object:getSpeed():setx() * -1)
  end
  return object
end
