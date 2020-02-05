local m = {
} 
function init(game,world)
  m.world = world;
end

function update(game, world, object, context)
  local player = {}
  player = context:get("player")
  if( player:getX() > object:getX() or player:getX() < object:getX() ) then
      object:setDx( object:getDx() * -1)
  end
  return object
end
